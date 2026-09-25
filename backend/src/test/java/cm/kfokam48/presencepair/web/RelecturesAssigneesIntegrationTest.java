package cm.kfokam48.presencepair.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Etudiant;
import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;

/** Issue #8 — EF6 : GET /api/etudiants/{id}/relectures. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RelecturesAssigneesIntegrationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    PromotionRepository promotions;
    @Autowired
    EtudiantRepository etudiants;
    @Autowired
    SessionCoursRepository sessions;
    @Autowired
    ExerciceRepository exercices;
    @Autowired
    RelectureRepository relectures;

    @Test
    void relecturesAFaireDAbord_avecLeLienARelire() throws Exception {
        Promotion promo = promotions.save(new Promotion("Promo EF6"));
        Etudiant alice = etudiants.save(new Etudiant("Alice", promo));
        Etudiant bob = etudiants.save(new Etudiant("Bob", promo));
        Etudiant chloe = etudiants.save(new Etudiant("Chloe", promo));
        SessionCours session = sessions.save(new SessionCours("React", promo, "RELU23", Instant.now()));
        Instant t0 = Instant.parse("2026-09-25T08:00:00Z");

        Exercice exoAlice = exercices.save(new Exercice(session, alice, "https://ex.cm/alice", t0));
        Relecture rendue = new Relecture(exoAlice, bob, t0.plusSeconds(60));
        rendue.rendre(15, "Bien", t0.plusSeconds(120));
        relectures.save(rendue);
        Exercice exoChloe = exercices.save(new Exercice(session, chloe, "https://ex.cm/chloe", t0));
        relectures.save(new Relecture(exoChloe, bob, t0));

        mvc.perform(get("/api/etudiants/{id}/relectures", bob.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].lien").value("https://ex.cm/chloe"))
                .andExpect(jsonPath("$[0].statut").value("A_FAIRE"))
                .andExpect(jsonPath("$[0].sessionTitre").value("React"))
                .andExpect(jsonPath("$[1].statut").value("RENDUE"))
                .andExpect(jsonPath("$[1].note").value(15));

        mvc.perform(get("/api/etudiants/{id}/relectures", alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void etudiantInconnu_404() throws Exception {
        mvc.perform(get("/api/etudiants/{id}/relectures", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ETUDIANT_INCONNU"));
    }
}
