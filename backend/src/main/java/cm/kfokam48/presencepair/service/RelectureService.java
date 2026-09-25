package cm.kfokam48.presencepair.service;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.StatutRelecture;
import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.web.dto.RelectureDto;

@Service
@Transactional
public class RelectureService {

    private final RelectureRepository relectures;
    private final EtudiantRepository etudiants;
    private final Clock horloge;

    public RelectureService(RelectureRepository relectures, EtudiantRepository etudiants, Clock horloge) {
        this.relectures = relectures;
        this.etudiants = etudiants;
        this.horloge = horloge;
    }

    /** EF6 : les exercices que l'étudiant doit relire. */
    @Transactional(readOnly = true)
    public List<RelectureDto> assigneesA(Long etudiantId) {
        if (!etudiants.existsById(etudiantId)) {
            throw new ErreurMetierException(CodeErreur.ETUDIANT_INCONNU);
        }
        return relectures.assigneesA(etudiantId).stream().map(RelectureDto::de).toList();
    }

    /**
     * EF7 : le relecteur rend sa relecture ; elle devient définitive (RG10). L'exercice passe
     * PARTIELLEMENT_RELU à la première relecture rendue, RELU à la seconde (RG16, v2).
     *
     * @param etudiantCourant étudiant sélectionné dans l'écran (en-tête X-Etudiant-Id), facultatif
     */
    public RelectureDto rendre(Long relectureId, int note, String commentaire, Long etudiantCourant) {
        Relecture relecture = modifiablePar(relectureId, etudiantCourant);
        relecture.rendre(note, commentaire.trim(), horloge.instant());
        relecture.getExercice().prendreEnCompteRelecturesRendues(
                relectures.countByExerciceIdAndStatut(relecture.getExercice().getId(), StatutRelecture.RENDUE));
        return RelectureDto.de(relecture);
    }

    /** Contrôles communs à toute écriture sur une relecture, dans l'ordre du contrat : 404, 403, 409. */
    private Relecture modifiablePar(Long relectureId, Long etudiantCourant) {
        Relecture relecture = relectures.findById(relectureId)
                .orElseThrow(() -> new ErreurMetierException(CodeErreur.RELECTURE_INCONNUE));
        Long auteurId = relecture.getExercice().getEtudiant().getId();

        // RG5 : ni l'étudiant qui envoie, ni le relecteur enregistré ne peuvent être l'auteur
        if (auteurId.equals(etudiantCourant) || auteurId.equals(relecture.getRelecteur().getId())) {
            throw new ErreurMetierException(CodeErreur.AUTO_RELECTURE);
        }
        if (etudiantCourant != null && !etudiantCourant.equals(relecture.getRelecteur().getId())) {
            throw new ErreurMetierException(CodeErreur.RELECTEUR_NON_ASSIGNE);
        }
        if (relecture.estRendue()) {
            throw new ErreurMetierException(CodeErreur.RELECTURE_DEJA_RENDUE); // RG10
        }
        if (relecture.getExercice().getSession().estCloturee()) {
            throw new ErreurMetierException(CodeErreur.SESSION_CLOTUREE); // RG11
        }
        return relecture;
    }
}
