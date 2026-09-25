package cm.kfokam48.presencepair.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Etudiant;
import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;

/** Issue #6 — EF4 : POST /api/exercices et liste des sessions d'une promotion. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ExerciceControllerIntegrationTest {

    private static final String LIEN = "https://github.com/alice/exercice";

    @Autowired
    MockMvc mvc;
    @Autowired
    PromotionRepository promotions;
    @Autowired
    EtudiantRepository etudiants;
    @Autowired
    SessionCoursRepository sessions;

    Promotion promo;
    Etudiant alice;
    Etudiant intrus;
    SessionCours session;

    @BeforeEach
    void donnees() {
        promo = promotions.save(new Promotion("Promo EF4"));
        Promotion autre = promotions.save(new Promotion("Autre promo EF4"));
        alice = etudiants.save(new Etudiant("Alice", promo));
        intrus = etudiants.save(new Etudiant("Intrus", autre));
        // Code expiré depuis longtemps : le dépôt reste possible jusqu'à la clôture (RG12)
        session = sessions.save(new SessionCours("Spring", promo, "EXO234", Instant.now().minus(Duration.ofDays(1))));
    }

    @Test
    void depot_201_statutDepose_memeApresExpirationDuCode() throws Exception {
        deposer(session.getId(), alice.getId(), LIEN)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.statut").value("DEPOSE"));
    }

    @Test
    void rg13_secondDepot_409() throws Exception {
        deposer(session.getId(), alice.getId(), LIEN).andExpect(status().isCreated());
        erreur(deposer(session.getId(), alice.getId(), LIEN), "EXERCICE_DEJA_DEPOSE").andExpect(status().isConflict());
    }

    @Test
    void rg18_lienInvalide_400() throws Exception {
        erreur(deposer(session.getId(), alice.getId(), "github.com/sans-schema"), "LIEN_INVALIDE")
                .andExpect(status().isBadRequest());
    }

    @Test
    void rg12_sessionCloturee_409() throws Exception {
        session.cloturer(Instant.now());
        erreur(deposer(session.getId(), alice.getId(), LIEN), "SESSION_CLOTUREE").andExpect(status().isConflict());
    }

    @Test
    void sessionInconnue_400() throws Exception {
        erreur(deposer(999_999L, alice.getId(), LIEN), "SESSION_INCONNUE").andExpect(status().isBadRequest());
    }

    @Test
    void rg19_etudiantDUneAutrePromotion_400() throws Exception {
        erreur(deposer(session.getId(), intrus.getId(), LIEN), "ETUDIANT_INCONNU").andExpect(status().isBadRequest());
    }

    @Test
    void sessionsDeLaPromotion_sansLeCode() throws Exception {
        mvc.perform(get("/api/promotions/{id}/sessions", promo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre").value("Spring"))
                .andExpect(jsonPath("$[0].cloturee").value(false))
                .andExpect(jsonPath("$[0].code").doesNotExist());
    }

    private ResultActions deposer(Long sessionId, Long etudiantId, String lien) throws Exception {
        return mvc.perform(post("/api/exercices").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"sessionId\": " + sessionId + ", \"etudiantId\": " + etudiantId + ", \"lien\": \"" + lien
                        + "\" }"));
    }

    private static ResultActions erreur(ResultActions resultat, String code) throws Exception {
        return resultat.andExpect(jsonPath("$.code").value(code)).andExpect(jsonPath("$.message").isNotEmpty());
    }
}
