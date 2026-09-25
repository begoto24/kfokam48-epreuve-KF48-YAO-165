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
import cm.kfokam48.presencepair.domain.Presence;
import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.domain.SourcePresence;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.PresenceRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;

/** Issue #10 — EF8, RG11, RG16 : opération imposée GET /api/tableau. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TableauIntegrationTest {

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
    PresenceRepository presences;
    @Autowired
    ExerciceRepository exercices;
    @Autowired
    RelectureRepository relectures;

    /**
     * Alice : 3 sessions présentes, 3 exercices notés 14, 15, 15 -> moyenne 14.67.
     * Bob : relecteur des 3 ; 1 relecture en brouillon (session clôturée) -> 1 en attente, brouillon hors moyenne.
     * Chloe : rien -> 0, 0, null, 0.
     */
    @Test
    void tableauAgregeParEtudiant() throws Exception {
        Promotion promo = promotions.save(new Promotion("Promo EF8"));
        Promotion autre = promotions.save(new Promotion("Autre promo EF8"));
        Etudiant alice = etudiants.save(new Etudiant("Alice", promo));
        Etudiant bob = etudiants.save(new Etudiant("Bob", promo));
        etudiants.save(new Etudiant("Chloe", promo));
        Etudiant intrus = etudiants.save(new Etudiant("Intrus", autre));

        int[] notes = { 14, 15, 15 };
        for (int i = 0; i < notes.length; i++) {
            SessionCours s = sessions.save(new SessionCours("S" + i, promo, "TAB" + i + "ZZ", T));
            presences.save(new Presence(s, alice, SourcePresence.ETUDIANT, T));
            Relecture r = relectures.save(new Relecture(
                    exercices.save(new Exercice(s, alice, "https://ex.cm/" + i, T)), bob, T));
            r.rendre(notes[i], "ok", T);
        }
        SessionCours cloturee = sessions.save(new SessionCours("Close", promo, "TABCLO", T));
        Exercice exoEnAttente = exercices.save(new Exercice(cloturee, alice, "https://ex.cm/attente", T));
        relectures.save(new Relecture(exoEnAttente, bob, T)).enregistrerBrouillon(2, "brouillon", T);
        cloturee.cloturer(T);

        SessionCours sessionAutre = sessions.save(new SessionCours("Autre", autre, "TABAUT", T));
        presences.save(new Presence(sessionAutre, intrus, SourcePresence.ETUDIANT, T));

        mvc.perform(get("/api/tableau").param("promotionId", promo.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].nom").value("Alice"))
                .andExpect(jsonPath("$[0].etudiantId").value(alice.getId()))
                .andExpect(jsonPath("$[0].presences").value(3))
                .andExpect(jsonPath("$[0].exercicesDeposes").value(4))
                .andExpect(jsonPath("$[0].moyenne").value(14.67))
                .andExpect(jsonPath("$[0].relecturesEnAttente").value(0))
                .andExpect(jsonPath("$[1].nom").value("Bob"))
                .andExpect(jsonPath("$[1].moyenne").isEmpty())
                .andExpect(jsonPath("$[1].relecturesEnAttente").value(1))
                .andExpect(jsonPath("$[2].nom").value("Chloe"))
                .andExpect(jsonPath("$[2].presences").value(0))
                .andExpect(jsonPath("$[2].exercicesDeposes").value(0))
                .andExpect(jsonPath("$[2].relecturesEnAttente").value(0));
    }

    @Test
    void promotionInconnue_404() throws Exception {
        mvc.perform(get("/api/tableau").param("promotionId", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }

    @Test
    void promotionIdAbsent_400() throws Exception {
        mvc.perform(get("/api/tableau"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CHAMP_MANQUANT"));
    }
}
