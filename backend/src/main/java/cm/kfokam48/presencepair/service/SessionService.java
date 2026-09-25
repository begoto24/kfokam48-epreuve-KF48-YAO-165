package cm.kfokam48.presencepair.service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.domain.SessionCours;
import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;
import cm.kfokam48.presencepair.web.dto.SessionOuverteDto;

@Service
@Transactional
public class SessionService {

    private final SessionCoursRepository sessions;
    private final PromotionRepository promotions;
    private final GenerateurCode generateur;
    private final Clock horloge;

    public SessionService(SessionCoursRepository sessions, PromotionRepository promotions,
            GenerateurCode generateur, Clock horloge) {
        this.sessions = sessions;
        this.promotions = promotions;
        this.generateur = generateur;
        this.horloge = horloge;
    }

    /** EF1 : ouvre une session ; son code expire 15 minutes plus tard (RG1). */
    public SessionOuverteDto ouvrir(String titre, Long promotionId) {
        Promotion promotion = promotions.findById(promotionId)
                .orElseThrow(() -> ErreurMetierException.referenceInvalide(CodeErreur.PROMOTION_INCONNUE));
        Instant maintenant = horloge.instant().truncatedTo(ChronoUnit.SECONDS);
        SessionCours session = sessions.save(new SessionCours(titre.trim(), promotion, codeUnique(), maintenant));
        return SessionOuverteDto.de(session);
    }

    private String codeUnique() {
        String code;
        do {
            code = generateur.generer();
        } while (sessions.existsByCode(code)); // RG20 : unicité, doublée d'une contrainte UNIQUE en base
        return code;
    }
}
