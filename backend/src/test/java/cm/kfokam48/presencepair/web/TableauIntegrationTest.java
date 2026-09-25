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

/** Issues #10 et #35 — EF8, RG11, RG16 (v2) : opération imposée GET /api/tableau. */
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
     * RG16 (v2) — note retenue = moyenne des relectures rendues, provisoire s'il n'y en a qu'une.
     * <ul>
     * <li>Alice : E1 {14} provisoire ; E2 {15, 17} = 16 ; E3 (session clôturée) {15} + un brouillon ignoré,
     * provisoire -> moyenne (14 + 16 + 15) / 3 = 15.0, provisoire ; 3 présences ; 3 exercices.</li>
     * <li>Bob : E4 {10, 13} = 11.5, définitive ; aucune relecture en attente.</li>
     * <li>Chloe : aucun exercice -> moyenne null ; 1 relecture en attente (le brouillon de E3, RG11).</li>
     * <li>David : rien.</li>
     * </ul>
     */
    @Test
    void tableauAvecNotesRetenuesEtProvisoires() throws Exception {
        Promotion promo = promotions.save(new Promotion("Promo EF8"));
        Promotion autre = promotions.save(new Promotion("Autre promo EF8"));
        Etudiant alice = etudiants.save(new Etudiant("Alice", promo));
        Etudiant bob = etudiants.save(new Etudiant("Bob", promo));
        Etudiant chloe = etudiants.save(new Etudiant("Chloe", promo));
        Etudiant david = etudiants.save(new Etudiant("David", promo));
        Etudiant intrus = etudiants.save(new Etudiant("Intrus", autre));

        SessionCours s0 = session(promo, "TAB0ZZ", alice);
        SessionCours s1 = session(promo, "TAB1ZZ", alice);
        SessionCours s2 = session(promo, "TAB2ZZ", alice);

        rendue(exercice(s0, alice), bob, 14);
        Exercice e2 = exercice(s1, alice);
        rendue(e2, bob, 15);
        rendue(e2, chloe, 17);
        Exercice e3 = exercice(s2, alice);
        rendue(e3, bob, 15);
        relectures.save(new Relecture(e3, chloe, T)).enregistrerBrouillon(2, "brouillon", T);
        s2.cloturer(T);
        Exercice e4 = exercice(s0, bob);
        rendue(e4, alice, 10);
        rendue(e4, david, 13);

        SessionCours sessionAutre = sessions.save(new SessionCours("Autre", autre, "TABAUT", T));
        presences.save(new Presence(sessionAutre, intrus, SourcePresence.ETUDIANT, T));

        mvc.perform(get("/api/tableau").param("promotionId", promo.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].nom").value("Alice"))
                .andExpect(jsonPath("$[0].etudiantId").value(alice.getId()))
                .andExpect(jsonPath("$[0].presences").value(3))
                .andExpect(jsonPath("$[0].exercicesDeposes").value(3))
                .andExpect(jsonPath("$[0].moyenne").value(15.0))
                .andExpect(jsonPath("$[0].moyenneProvisoire").value(true))
                .andExpect(jsonPath("$[0].relecturesEnAttente").value(0))
                .andExpect(jsonPath("$[1].nom").value("Bob"))
                .andExpect(jsonPath("$[1].moyenne").value(11.5))
                .andExpect(jsonPath("$[1].moyenneProvisoire").value(false))
                .andExpect(jsonPath("$[1].relecturesEnAttente").value(0))
                .andExpect(jsonPath("$[2].nom").value("Chloe"))
                .andExpect(jsonPath("$[2].moyenne").isEmpty())
                .andExpect(jsonPath("$[2].moyenneProvisoire").value(false))
                .andExpect(jsonPath("$[2].relecturesEnAttente").value(1))
                .andExpect(jsonPath("$[3].nom").value("David"))
                .andExpect(jsonPath("$[3].presences").value(0))
                .andExpect(jsonPath("$[3].exercicesDeposes").value(0));
    }

    private SessionCours session(Promotion promo, String code, Etudiant present) {
        SessionCours s = sessions.save(new SessionCours("S " + code, promo, code, T));
        presences.save(new Presence(s, present, SourcePresence.ETUDIANT, T));
        return s;
    }

    private Exercice exercice(SessionCours s, Etudiant auteur) {
        return exercices.save(new Exercice(s, auteur, "https://ex.cm/" + s.getCode() + auteur.getNom(), T));
    }

    private void rendue(Exercice exercice, Etudiant relecteur, int note) {
        relectures.save(new Relecture(exercice, relecteur, T)).rendre(note, "ok", T);
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
