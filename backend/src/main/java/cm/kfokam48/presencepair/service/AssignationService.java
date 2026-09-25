package cm.kfokam48.presencepair.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.domain.StatutExercice;
import cm.kfokam48.presencepair.domain.TirageRelecteur;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.PresenceRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;

/** EF5 : le système confie chaque exercice à un pair présent (RG5, RG6, RG7, RG17). */
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

    /** Tire un relecteur pour un exercice qui vient d'être déposé. Sans candidat, il reste DEPOSE (RG17). */
    public void assigner(Exercice exercice) {
        verrouiller(exercice.getSession());
        tirer(exercice);
    }

    /**
     * RG17 : appelé à chaque nouvelle présence, assigne les exercices restés sans relecteur.
     * La liste est lue APRÈS le verrou : une présence simultanée qui vient d'assigner un exercice
     * a déjà validé sa transaction, l'exercice n'est donc plus DEPOSE (bug #32).
     */
    public void assignerEnAttente(SessionCours session) {
        verrouiller(session);
        exercices.findBySessionIdAndStatutOrderByDeposeAtAsc(session.getId(), StatutExercice.DEPOSE)
                .forEach(this::tirer);
    }

    private void tirer(Exercice exercice) {
        if (exercice.getStatut() != StatutExercice.DEPOSE || exercice.getSession().estCloturee()) {
            return;
        }
        Long sessionId = exercice.getSession().getId();
        tirage.choisir(exercice.getEtudiant().getId(), presences.idsDesPresents(sessionId), charges(sessionId))
                .ifPresent(relecteurId -> {
                    relectures.save(new Relecture(exercice, etudiants.getReferenceById(relecteurId),
                            horloge.instant()));
                    exercice.marquerEnAttenteDeRelecture();
                });
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
