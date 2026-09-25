package cm.kfokam48.presencepair.web;

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

/**
 * Issue #5 — test d'intégration de l'endpoint imposé POST /api/presences (B6).
 * Chaque cas correspond à une sortie du diagramme D3.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PresenceControllerIntegrationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    PromotionRepository promotions;
    @Autowired
    EtudiantRepository etudiants;
    @Autowired
    SessionCoursRepository sessions;

    Etudiant alice;
    SessionCours sessionOuverte;
    SessionCours sessionExpiree;
    SessionCours sessionAutrePromotion;

    @BeforeEach
    void donnees() {
        Promotion promo = promotions.save(new Promotion("Promo EF3"));
        Promotion autre = promotions.save(new Promotion("Autre promo EF3"));
        alice = etudiants.save(new Etudiant("Alice", promo));
        Instant maintenant = Instant.now();
        sessionOuverte = sessions.save(new SessionCours("Ouverte", promo, "OUVR23", maintenant));
        sessionExpiree = sessions.save(new SessionCours("Expiree", promo, "EXPR23",
                maintenant.minus(Duration.ofMinutes(16))));
        sessionAutrePromotion = sessions.save(new SessionCours("Autre", autre, "AUTR23", maintenant));
    }

    @Test
    void codeValide_201_sourceEtudiant() throws Exception {
        marquer("OUVR23", alice.getId())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.sessionId").value(sessionOuverte.getId()))
                .andExpect(jsonPath("$.etudiantId").value(alice.getId()))
                .andExpect(jsonPath("$.source").value("ETUDIANT"));
    }

    @Test
    void rg20_saisieEnMinusculesAvecEspaces_201() throws Exception {
        marquer(" ouvr 23 ", alice.getId()).andExpect(status().isCreated());
    }

    @Test
    void rg3_dejaPresent_409() throws Exception {
        marquer("OUVR23", alice.getId()).andExpect(status().isCreated());
        erreur(marquer("OUVR23", alice.getId()), "DEJA_PRESENT").andExpect(status().isConflict());
    }

    @Test
    void rg1_codeExpire_410() throws Exception {
        erreur(marquer("EXPR23", alice.getId()), "CODE_EXPIRE").andExpect(status().isGone());
    }

    @Test
    void rg2_sessionCloturee_410() throws Exception {
        sessionOuverte.cloturer(Instant.now());
        erreur(marquer("OUVR23", alice.getId()), "CODE_EXPIRE").andExpect(status().isGone());
    }

    @Test
    void codeInconnu_400() throws Exception {
        erreur(marquer("ZZZZZZ", alice.getId()), "CODE_INCONNU").andExpect(status().isBadRequest());
    }

    @Test
    void rg19_codeDUneAutrePromotion_400CodeInconnu() throws Exception {
        erreur(marquer("AUTR23", alice.getId()), "CODE_INCONNU").andExpect(status().isBadRequest());
    }

    @Test
    void etudiantInconnu_400() throws Exception {
        erreur(marquer("OUVR23", 999_999L), "ETUDIANT_INCONNU").andExpect(status().isBadRequest());
    }

    @Test
    void codeAbsent_400ChampManquant() throws Exception {
        erreur(mvc.perform(post("/api/presences").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"etudiantId\": " + alice.getId() + " }")), "CHAMP_MANQUANT")
                .andExpect(status().isBadRequest());
    }

    private ResultActions marquer(String code, Long etudiantId) throws Exception {
        return mvc.perform(post("/api/presences").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"code\": \"" + code + "\", \"etudiantId\": " + etudiantId + " }"));
    }

    private static ResultActions erreur(ResultActions resultat, String code) throws Exception {
        return resultat.andExpect(jsonPath("$.code").value(code)).andExpect(jsonPath("$.message").isNotEmpty());
    }
}
