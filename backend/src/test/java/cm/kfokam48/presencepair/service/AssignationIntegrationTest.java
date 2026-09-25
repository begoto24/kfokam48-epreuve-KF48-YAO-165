package cm.kfokam48.presencepair.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Etudiant;
import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.domain.StatutExercice;
import cm.kfokam48.presencepair.domain.StatutRelecture;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;
import cm.kfokam48.presencepair.web.dto.ExerciceDeposeDto;

/** Issue #7 — EF5 : assignation au dépôt et file d'attente (RG5, RG6, RG7, RG17). */
@SpringBootTest
@Transactional
class AssignationIntegrationTest {

    private static final String LIEN = "https://github.com/exo";

    @Autowired
    PresenceService presenceService;
    @Autowired
    ExerciceService exerciceService;
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

    Etudiant alice;
    Etudiant bob;
    Etudiant chloe;
    SessionCours session;

    @BeforeEach
    void donnees() {
        Promotion promo = promotions.save(new Promotion("Promo EF5"));
        alice = etudiants.save(new Etudiant("Alice", promo));
        bob = etudiants.save(new Etudiant("Bob", promo));
        chloe = etudiants.save(new Etudiant("Chloe", promo));
        session = sessions.save(new SessionCours("Spring", promo, "ASGN23", Instant.now()));
    }

    @Test
    void unAutrePresent_leDepotEstAssigneTouteSuite() {
        presenceService.marquer("ASGN23", alice.getId());
        presenceService.marquer("ASGN23", bob.getId());

        ExerciceDeposeDto depot = exerciceService.deposer(session.getId(), alice.getId(), LIEN);

        assertThat(depot.statut()).isEqualTo(StatutExercice.EN_ATTENTE_RELECTURE);
        Relecture relecture = relectureDe(depot.id());
        assertThat(relecture.getRelecteur().getId()).isEqualTo(bob.getId()); // seul éligible, jamais Alice (RG5)
        assertThat(relecture.getStatut()).isEqualTo(StatutRelecture.A_FAIRE);
    }

    @Test
    void rg17_auteurSeulPresent_resteDepose_puisAssigneQuandUnPairArrive() {
        presenceService.marquer("ASGN23", alice.getId());
        ExerciceDeposeDto depot = exerciceService.deposer(session.getId(), alice.getId(), LIEN);
        assertThat(depot.statut()).isEqualTo(StatutExercice.DEPOSE);
        assertThat(relectures.findAll()).noneMatch(r -> r.getExercice().getId().equals(depot.id()));

        presenceService.marquer("ASGN23", bob.getId());

        Exercice exercice = exercices.findById(depot.id()).orElseThrow();
        assertThat(exercice.getStatut()).isEqualTo(StatutExercice.EN_ATTENTE_RELECTURE);
        assertThat(relectureDe(depot.id()).getRelecteur().getId()).isEqualTo(bob.getId());
    }

    @Test
    void unAbsentPeutDeposer_etEstReluParUnPresent() {
        presenceService.marquer("ASGN23", bob.getId());
        ExerciceDeposeDto depot = exerciceService.deposer(session.getId(), chloe.getId(), LIEN);
        assertThat(relectureDe(depot.id()).getRelecteur().getId()).isEqualTo(bob.getId());
    }

    @Test
    void rg6_unePresenceSupplementaireNeDonnePasDeSecondRelecteur() {
        presenceService.marquer("ASGN23", bob.getId());
        ExerciceDeposeDto depot = exerciceService.deposer(session.getId(), alice.getId(), LIEN);
        presenceService.marquer("ASGN23", chloe.getId());

        List<Relecture> relecturesDeLExercice = relectures.findAll().stream()
                .filter(r -> r.getExercice().getId().equals(depot.id())).toList();
        assertThat(relecturesDeLExercice).hasSize(1);
    }

    private Relecture relectureDe(Long exerciceId) {
        return relectures.findAll().stream().filter(r -> r.getExercice().getId().equals(exerciceId)).findFirst()
                .orElseThrow();
    }
}
