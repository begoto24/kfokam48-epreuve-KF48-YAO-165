package cm.kfokam48.presencepair.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.SessionCoursRepository;
import cm.kfokam48.presencepair.web.dto.EtudiantDto;
import cm.kfokam48.presencepair.web.dto.PromotionDto;
import cm.kfokam48.presencepair.web.dto.SessionDto;

@Service
@Transactional(readOnly = true)
public class PromotionService {

    private final PromotionRepository promotions;
    private final EtudiantRepository etudiants;
    private final SessionCoursRepository sessions;

    public PromotionService(PromotionRepository promotions, EtudiantRepository etudiants,
            SessionCoursRepository sessions) {
        this.promotions = promotions;
        this.etudiants = etudiants;
        this.sessions = sessions;
    }

    public List<PromotionDto> lister() {
        return promotions.findAllByOrderByNomAsc().stream().map(PromotionDto::de).toList();
    }

    /** EF2 : la liste dans laquelle l'étudiant choisit son nom (Q1). */
    public List<EtudiantDto> etudiants(Long promotionId) {
        verifierExistence(promotionId);
        return etudiants.findByPromotionIdOrderByNomAsc(promotionId).stream().map(EtudiantDto::de).toList();
    }

    /** Sessions de la promotion, la plus récente d'abord : l'étudiant y choisit celle de son dépôt. */
    public List<SessionDto> sessions(Long promotionId) {
        verifierExistence(promotionId);
        return sessions.findByPromotionIdOrderByOuvertureAtDesc(promotionId).stream().map(SessionDto::de).toList();
    }

    private void verifierExistence(Long promotionId) {
        if (!promotions.existsById(promotionId)) {
            throw new ErreurMetierException(CodeErreur.PROMOTION_INCONNUE);
        }
    }
}
