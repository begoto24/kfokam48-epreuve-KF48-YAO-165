package cm.kfokam48.presencepair.service;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Etudiant;
import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.Lien;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;
import cm.kfokam48.presencepair.web.dto.ExerciceDeposeDto;

@Service
@Transactional
public class ExerciceService {

    private final ExerciceRepository exercices;
    private final SessionCoursRepository sessions;
    private final EtudiantRepository etudiants;
    private final Clock horloge;

    public ExerciceService(ExerciceRepository exercices, SessionCoursRepository sessions,
            EtudiantRepository etudiants, Clock horloge) {
        this.exercices = exercices;
        this.sessions = sessions;
        this.etudiants = etudiants;
        this.horloge = horloge;
    }

    /** EF4 : dépôt possible jusqu'à la clôture, même après l'expiration du code (RG12). */
    public ExerciceDeposeDto deposer(Long sessionId, Long etudiantId, String lien) {
        if (!Lien.estValide(lien)) {
            throw new ErreurMetierException(CodeErreur.LIEN_INVALIDE); // RG18
        }
        SessionCours session = sessions.findById(sessionId)
                .orElseThrow(() -> ErreurMetierException.referenceInvalide(CodeErreur.SESSION_INCONNUE));
        Etudiant etudiant = etudiants.findById(etudiantId)
                .filter(e -> e.appartientA(session.getPromotion())) // RG19
                .orElseThrow(() -> ErreurMetierException.referenceInvalide(CodeErreur.ETUDIANT_INCONNU));

        if (session.estCloturee()) {
            throw new ErreurMetierException(CodeErreur.SESSION_CLOTUREE); // RG12
        }
        if (exercices.existsBySessionIdAndEtudiantId(sessionId, etudiantId)) {
            throw new ErreurMetierException(CodeErreur.EXERCICE_DEJA_DEPOSE); // RG13
        }
        Exercice exercice = exercices.save(new Exercice(session, etudiant, lien.trim(), horloge.instant()));
        return ExerciceDeposeDto.de(exercice);
    }
}
