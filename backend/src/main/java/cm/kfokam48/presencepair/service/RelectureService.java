package cm.kfokam48.presencepair.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public RelectureService(RelectureRepository relectures, EtudiantRepository etudiants) {
        this.relectures = relectures;
        this.etudiants = etudiants;
    }

    /** EF6 : les exercices que l'étudiant doit relire. */
    @Transactional(readOnly = true)
    public List<RelectureDto> assigneesA(Long etudiantId) {
        if (!etudiants.existsById(etudiantId)) {
            throw new ErreurMetierException(CodeErreur.ETUDIANT_INCONNU);
        }
        return relectures.assigneesA(etudiantId).stream().map(RelectureDto::de).toList();
    }
}
