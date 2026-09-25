package cm.kfokam48.presencepair.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

/** Issue #16 — EF14, RG8, RG16 (v2) : GET /api/etudiants/{id}/exercices. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MesExercicesIntegrationTest {

    private static final Instant T = Instant.parse("2026-09-25T08:00:00Z");

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
    void noteRetenueProvisoire_puisDefinitive_sansJamaisLesRelecteurs() throws Exception {
        Promotion promo = promotions.save(new Promotion("Promo EF14"));
        Etudiant alice = etudiants.save(new Etudiant("Alice", promo));
        Etudiant bob = etudiants.save(new Etudiant("Relecteur Bob", promo));
        Etudiant chloe = etudiants.save(new Etudiant("Relectrice Chloe", promo));
        SessionCours ancienne = sessions.save(new SessionCours("Ancienne", promo, "MESEX2", T));
        SessionCours recente = sessions.save(new SessionCours("Recente", promo, "MESEX3", T.plusSeconds(3600)));

        Exercice complet = exercices.save(new Exercice(ancienne, alice, "https://ex.cm/complet", T));
        relectures.save(new Relecture(complet, bob, T)).rendre(12, "Correct", T);
        relectures.save(new Relecture(complet, chloe, T)).rendre(15, "Bien", T.plusSeconds(1));

        Exercice partiel = exercices.save(new Exercice(recente, alice, "https://ex.cm/partiel", T.plusSeconds(3600)));
        relectures.save(new Relecture(partiel, bob, T)).rendre(14, "Clair", T);
        relectures.save(new Relecture(partiel, chloe, T)).enregistrerBrouillon(3, "brouillon cache", T);

        mvc.perform(get("/api/etudiants/{id}/exercices", alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sessionTitre").value("Recente"))
                .andExpect(jsonPath("$[0].note").value(14.0))
                .andExpect(jsonPath("$[0].noteProvisoire").value(true))
                .andExpect(jsonPath("$[0].commentaires.length()").value(1))
                .andExpect(jsonPath("$[0].commentaires[0]").value("Clair"))
                .andExpect(jsonPath("$[1].note").value(13.5))
                .andExpect(jsonPath("$[1].noteProvisoire").value(false))
                .andExpect(jsonPath("$[1].commentaires.length()").value(2))
                // RG8 : aucune trace des relecteurs, ni nom ni identifiant ; le brouillon reste invisible
                .andExpect(content().string(not(containsString("Bob"))))
                .andExpect(content().string(not(containsString("Chloe"))))
                .andExpect(content().string(not(containsString("relecteur"))))
                .andExpect(content().string(not(containsString("brouillon cache"))));
    }

    @Test
    void aucuneRelectureRendue_noteNulle() throws Exception {
        Promotion promo = promotions.save(new Promotion("Promo EF14 bis"));
        Etudiant alice = etudiants.save(new Etudiant("Alice", promo));
        SessionCours s = sessions.save(new SessionCours("S", promo, "MESEX4", T));
        exercices.save(new Exercice(s, alice, "https://ex.cm/rien", T));

        mvc.perform(get("/api/etudiants/{id}/exercices", alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].note").isEmpty())
                .andExpect(jsonPath("$[0].noteProvisoire").value(false))
                .andExpect(jsonPath("$[0].commentaires.length()").value(0));
    }

    @Test
    void etudiantInconnu_404() throws Exception {
        mvc.perform(get("/api/etudiants/{id}/exercices", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ETUDIANT_INCONNU"));
    }
}
