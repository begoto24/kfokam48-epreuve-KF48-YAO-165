package cm.kfokam48.presencepair.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.domain.StatutExercice;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;

/** Issue #9 — EF7 : opération imposée POST /api/relectures/{id}, tous ses codes. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RendreRelectureIntegrationTest {

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

    Etudiant auteur;
    Etudiant relecteur;
    Etudiant autre;
    SessionCours session;
    Exercice exercice;
    Relecture relecture;

    @BeforeEach
    void donnees() {
        Promotion promo = promotions.save(new Promotion("Promo EF7"));
        auteur = etudiants.save(new Etudiant("Auteur", promo));
        relecteur = etudiants.save(new Etudiant("Relecteur", promo));
        autre = etudiants.save(new Etudiant("Autre", promo));
        session = sessions.save(new SessionCours("Spring", promo, "NOTE23", Instant.now()));
        exercice = exercices.save(new Exercice(session, auteur, "https://ex.cm/auteur", Instant.now()));
        exercice.marquerEnAttenteDeRelecture();
        relecture = relectures.save(new Relecture(exercice, relecteur, Instant.now()));
    }

    @Test
    void rendre_200_relectureRendueEtExerciceRelu() throws Exception {
        rendre(relecture.getId(), "{ \"note\": 14, \"commentaire\": \"Solide\" }", relecteur.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("RENDUE"))
                .andExpect(jsonPath("$.note").value(14));
        assertThat(exercice.getStatut()).isEqualTo(StatutExercice.RELU);
    }

    @Test
    void rendre_sansEnTete_200() throws Exception {
        rendre(relecture.getId(), "{ \"note\": 0, \"commentaire\": \"Vide\" }", null).andExpect(status().isOk());
    }

    @Test
    void rg9_noteDecimale_400_pasArrondie() throws Exception {
        erreur(rendre(relecture.getId(), "{ \"note\": 12.5, \"commentaire\": \"x\" }", null), "NOTE_INVALIDE")
                .andExpect(status().isBadRequest());
        assertThat(relecture.estRendue()).isFalse();
    }

    @Test
    void rg9_noteHorsBornes_400() throws Exception {
        erreur(rendre(relecture.getId(), "{ \"note\": 21, \"commentaire\": \"x\" }", null), "NOTE_INVALIDE")
                .andExpect(status().isBadRequest());
        erreur(rendre(relecture.getId(), "{ \"note\": -1, \"commentaire\": \"x\" }", null), "NOTE_INVALIDE")
                .andExpect(status().isBadRequest());
    }

    @Test
    void commentaireAbsent_400ChampManquant() throws Exception {
        erreur(rendre(relecture.getId(), "{ \"note\": 12 }", null), "CHAMP_MANQUANT").andExpect(status().isBadRequest());
    }

    @Test
    void rg5_lAuteurEssaieDeSeRelire_403() throws Exception {
        erreur(rendre(relecture.getId(), "{ \"note\": 20, \"commentaire\": \"Parfait\" }", auteur.getId()),
                "AUTO_RELECTURE").andExpect(status().isForbidden());
    }

    @Test
    void unAutreEtudiantQueLeRelecteur_403() throws Exception {
        erreur(rendre(relecture.getId(), "{ \"note\": 10, \"commentaire\": \"x\" }", autre.getId()),
                "RELECTEUR_NON_ASSIGNE").andExpect(status().isForbidden());
    }

    @Test
    void rg10_relectureDejaRendue_409() throws Exception {
        rendre(relecture.getId(), "{ \"note\": 14, \"commentaire\": \"Solide\" }", null).andExpect(status().isOk());
        erreur(rendre(relecture.getId(), "{ \"note\": 18, \"commentaire\": \"Je change\" }", null),
                "RELECTURE_DEJA_RENDUE").andExpect(status().isConflict());
        assertThat(relecture.getNote()).isEqualTo(14);
    }

    @Test
    void rg11_sessionCloturee_409() throws Exception {
        session.cloturer(Instant.now());
        erreur(rendre(relecture.getId(), "{ \"note\": 14, \"commentaire\": \"x\" }", null), "SESSION_CLOTUREE")
                .andExpect(status().isConflict());
    }

    @Test
    void relectureInconnue_404() throws Exception {
        erreur(rendre(999_999L, "{ \"note\": 14, \"commentaire\": \"x\" }", null), "RELECTURE_INCONNUE")
                .andExpect(status().isNotFound());
    }

    private ResultActions rendre(Long id, String corps, Long etudiantCourant) throws Exception {
        var requete = post("/api/relectures/{id}", id).contentType(MediaType.APPLICATION_JSON).content(corps);
        if (etudiantCourant != null) {
            requete.header(RelectureController.ENTETE_ETUDIANT, etudiantCourant);
        }
        return mvc.perform(requete);
    }

    private static ResultActions erreur(ResultActions resultat, String code) throws Exception {
        return resultat.andExpect(jsonPath("$.code").value(code)).andExpect(jsonPath("$.message").isNotEmpty());
    }
}
