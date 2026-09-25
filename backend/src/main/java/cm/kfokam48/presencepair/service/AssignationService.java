package cm.kfokam48.presencepair.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
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

/** EF5 : le système confie chaque exercice à un pair présent (RG5, RG6, RG7, RG17). */
@Service
@Transactional
public class AssignationService {

    private final PresenceRepository presences;
    private final RelectureRepository relectures;
    private final ExerciceRepository exercices;
    private final EtudiantRepository etudiants;
    private final Clock horloge;
    private final TirageRelecteur tirage = new TirageRelecteur(new SecureRandom());

    public AssignationService(PresenceRepository presences, RelectureRepository relectures,
            ExerciceRepository exercices, EtudiantRepository etudiants, Clock horloge) {
        this.presences = presences;
        this.relectures = relectures;
        this.exercices = exercices;
        this.etudiants = etudiants;
        this.horloge = horloge;
    }

    /** Tire un relecteur pour un exercice encore DEPOSE. Sans candidat, il reste DEPOSE (RG17). */
    public void assigner(Exercice exercice) {
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

    /** RG17 : appelé à chaque nouvelle présence, assigne les exercices restés sans relecteur. */
    public void assignerEnAttente(SessionCours session) {
        List<Exercice> enAttente = exercices.findBySessionIdAndStatutOrderByDeposeAtAsc(session.getId(),
                StatutExercice.DEPOSE);
        enAttente.forEach(this::assigner);
    }

    private Map<Long, Long> charges(Long sessionId) {
        Map<Long, Long> charges = new HashMap<>();
        for (Object[] ligne : relectures.chargesParRelecteur(sessionId)) {
            charges.put((Long) ligne[0], (Long) ligne[1]);
        }
        return charges;
    }
}
