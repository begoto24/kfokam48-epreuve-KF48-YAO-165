package cm.kfokam48.presencepair.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.domain.TirageRelecteur;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.PresenceRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;

/** EF5 (v2) : le système confie chaque exercice à DEUX pairs présents différents (RG5, RG6, RG7, RG17). */
@Service
@Transactional
public class AssignationService {

    private final PresenceRepository presences;
    private final RelectureRepository relectures;
    private final ExerciceRepository exercices;
    private final EtudiantRepository etudiants;
    private final SessionCoursRepository sessions;
    private final Clock horloge;
    private final TirageRelecteur tirage = new TirageRelecteur(new SecureRandom());

    public AssignationService(PresenceRepository presences, RelectureRepository relectures,
            ExerciceRepository exercices, EtudiantRepository etudiants, SessionCoursRepository sessions,
            Clock horloge) {
        this.presences = presences;
        this.relectures = relectures;
        this.exercices = exercices;
        this.etudiants = etudiants;
        this.sessions = sessions;
        this.horloge = horloge;
    }

    /** Tire les relecteurs d'un exercice qui vient d'être déposé. Sans candidat, il reste DEPOSE (RG17). */
    public void assigner(Exercice exercice) {
        verrouiller(exercice.getSession());
        completer(exercice);
    }

    /**
     * RG17 : appelé à chaque nouvelle présence, complète les exercices qui n'ont pas encore leurs deux
     * relecteurs. La liste est lue APRÈS le verrou : une présence simultanée a déjà validé ses
     * assignations, elles sont donc prises en compte (bug #32).
     */
    public void assignerEnAttente(SessionCours session) {
        verrouiller(session);
        exercices.aCompleter(session.getId(), Exercice.RELECTEURS_PAR_EXERCICE).forEach(this::completer);
    }

    /** Tire autant de relecteurs que nécessaire pour atteindre deux, tant qu'il reste des candidats. */
    private void completer(Exercice exercice) {
        if (exercice.getSession().estCloturee()) {
            return;
        }
        Long sessionId = exercice.getSession().getId();
        List<Long> presents = presences.idsDesPresents(sessionId);
        Map<Long, Long> charges = charges(sessionId);
        List<Long> relecteurs = new ArrayList<>(relectures.relecteursDe(exercice.getId()));

        while (relecteurs.size() < Exercice.RELECTEURS_PAR_EXERCICE) {
            Optional<Long> tire = tirage.choisir(exercice.getEtudiant().getId(), presents, charges, relecteurs);
            if (tire.isEmpty()) {
                return; // pas assez de pairs éligibles : le reste sera tiré à la prochaine présence (RG17)
            }
            Long relecteurId = tire.get();
            relectures.save(new Relecture(exercice, etudiants.getReferenceById(relecteurId), horloge.instant()));
            relecteurs.add(relecteurId);
            charges.merge(relecteurId, 1L, Long::sum);
            exercice.marquerEnAttenteDeRelecture();
        }
    }

    /** Bug #32 : une seule assignation à la fois par session, les autres attendent leur tour. */
    private void verrouiller(SessionCours session) {
        sessions.verrouiller(session.getId());
    }

    private Map<Long, Long> charges(Long sessionId) {
        Map<Long, Long> charges = new HashMap<>();
        for (Object[] ligne : relectures.chargesParRelecteur(sessionId)) {
            charges.put((Long) ligne[0], (Long) ligne[1]);
        }
        return charges;
    }
}
