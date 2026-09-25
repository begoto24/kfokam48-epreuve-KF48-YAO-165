package cm.kfokam48.presencepair.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cm.kfokam48.presencepair.domain.Etudiant;
import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.PresenceRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;

/**
 * Bug #32 — « deux étudiants tapent le code presque en même temps, un seul apparaît ».
 * <p>
 * Scénario : A dépose seul (exercice DEPOSE, RG17), puis B et C marquent leur présence au même instant.
 * Les deux transactions tentent d'assigner l'exercice de A ; la contrainte RG6 fait échouer l'une
 * d'elles, ce qui annule aussi sa présence.
 * <p>
 * Pas de @Transactional : les deux présences doivent être de vraies transactions concurrentes.
 */
@SpringBootTest
class PresencesSimultaneesTest {

    private static final int REPETITIONS = 20;

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
    PresenceRepository presences;
    @Autowired
    RelectureRepository relectures;
    @Autowired
    GenerateurCode generateur;

    ExecutorService executeur;
    Promotion promo;

    @BeforeEach
    void preparer() {
        executeur = Executors.newFixedThreadPool(2);
        promo = promotions.save(new Promotion("Promo bug 32 " + System.nanoTime()));
    }

    @AfterEach
    void arreter() {
        executeur.shutdownNow();
    }

    @Test
    void deuxPresencesSimultanees_lesDeuxSontEnregistrees() throws Exception {
        List<String> echecs = new CopyOnWriteArrayList<>();

        for (int i = 0; i < REPETITIONS; i++) {
            Etudiant a = etudiants.save(new Etudiant("A" + i, promo));
            Etudiant b = etudiants.save(new Etudiant("B" + i, promo));
            Etudiant c = etudiants.save(new Etudiant("C" + i, promo));
            String code = generateur.generer();
            SessionCours session = sessions.save(new SessionCours("Session " + i, promo, code, Instant.now()));

            presenceService.marquer(code, a.getId());
            exerciceService.deposer(session.getId(), a.getId(), "https://ex.cm/a" + i); // reste DEPOSE

            CyclicBarrier depart = new CyclicBarrier(2);
            Future<?> presenceB = executeur.submit(simultanement(depart, () -> presenceService.marquer(code, b.getId())));
            Future<?> presenceC = executeur.submit(simultanement(depart, () -> presenceService.marquer(code, c.getId())));
            attendre(presenceB, "B", i, echecs);
            attendre(presenceC, "C", i, echecs);

            if (!presences.existsBySessionIdAndEtudiantId(session.getId(), b.getId())
                    || !presences.existsBySessionIdAndEtudiantId(session.getId(), c.getId())) {
                echecs.add("répétition " + i + " : une présence manque en base");
            }
        }

        assertThat(echecs).as("présences perdues sur %d répétitions", REPETITIONS).isEmpty();
    }

    private static Callable<Object> simultanement(CyclicBarrier depart, Callable<Object> action) {
        return () -> {
            depart.await(5, TimeUnit.SECONDS);
            return action.call();
        };
    }

    private static void attendre(Future<?> presence, String qui, int i, List<String> echecs) {
        try {
            presence.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            echecs.add("répétition " + i + " : présence de " + qui + " refusée ("
                    + (e.getCause() != null ? e.getCause().getClass().getSimpleName() : e) + ")");
        }
    }
}
