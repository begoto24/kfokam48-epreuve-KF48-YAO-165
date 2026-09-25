package cm.kfokam48.presencepair.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Etudiant;
import cm.kfokam48.presencepair.domain.Presence;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.domain.SourcePresence;
import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.PresenceRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;
import cm.kfokam48.presencepair.web.dto.PresenceDto;

/** Marquage de présence : ordre des contrôles du diagramme D3. */
@Service
@Transactional
public class PresenceService {

    private final PresenceRepository presences;
    private final SessionCoursRepository sessions;
    private final EtudiantRepository etudiants;
    private final Clock horloge;

    public PresenceService(PresenceRepository presences, SessionCoursRepository sessions,
            EtudiantRepository etudiants, Clock horloge) {
        this.presences = presences;
        this.sessions = sessions;
        this.etudiants = etudiants;
        this.horloge = horloge;
    }

    /** EF3 : l'étudiant marque sa présence avec le code affiché par le formateur. */
    public PresenceDto marquer(String code, Long etudiantId) {
        Instant maintenant = horloge.instant();
        Etudiant etudiant = etudiants.findById(etudiantId)
                .orElseThrow(() -> ErreurMetierException.referenceInvalide(CodeErreur.ETUDIANT_INCONNU));

        // RG20 : saisie normalisée ; RG19 : un code d'une autre promotion est traité comme inconnu
        SessionCours session = sessions.findByCode(GenerateurCode.normaliser(code))
                .filter(s -> etudiant.appartientA(s.getPromotion()))
                .orElseThrow(() -> new ErreurMetierException(CodeErreur.CODE_INCONNU));

        if (!session.codeUtilisableA(maintenant)) {
            throw new ErreurMetierException(CodeErreur.CODE_EXPIRE); // RG1, RG2
        }
        if (presences.existsBySessionIdAndEtudiantId(session.getId(), etudiant.getId())) {
            throw new ErreurMetierException(CodeErreur.DEJA_PRESENT); // RG3
        }
        Presence presence = presences.save(new Presence(session, etudiant, SourcePresence.ETUDIANT, maintenant));
        return PresenceDto.de(presence);
    }
}
