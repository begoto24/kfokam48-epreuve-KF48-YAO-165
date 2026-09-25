package cm.kfokam48.presencepair.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.NoteRetenue;
import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.ExerciceRepository;
import cm.kfokam48.presencepair.repository.RelectureRepository;
import cm.kfokam48.presencepair.web.dto.ExerciceEtudiantDto;

/** EF14 : l'étudiant voit la note retenue et les commentaires de ses exercices, jamais ses relecteurs (RG8). */
@Service
@Transactional(readOnly = true)
public class MesExercicesService {

    private final EtudiantRepository etudiants;
    private final ExerciceRepository exercices;
    private final RelectureRepository relectures;

    public MesExercicesService(EtudiantRepository etudiants, ExerciceRepository exercices,
            RelectureRepository relectures) {
        this.etudiants = etudiants;
        this.exercices = exercices;
        this.relectures = relectures;
    }

    public List<ExerciceEtudiantDto> deLEtudiant(Long etudiantId) {
        if (!etudiants.existsById(etudiantId)) {
            throw new ErreurMetierException(CodeErreur.ETUDIANT_INCONNU);
        }
        // Deux requêtes au total : les exercices, puis toutes leurs relectures rendues
        Map<Long, List<Relecture>> rendues = relectures.rendues(etudiantId).stream()
                .collect(Collectors.groupingBy(r -> r.getExercice().getId()));
        return exercices.deLAuteur(etudiantId).stream()
                .map(e -> versDto(e, rendues.getOrDefault(e.getId(), List.of())))
                .toList();
    }

    private static ExerciceEtudiantDto versDto(Exercice e, List<Relecture> rendues) {
        Double note = null;
        boolean provisoire = false;
        if (!rendues.isEmpty()) {
            double moyenne = rendues.stream().mapToInt(Relecture::getNote).average().orElseThrow();
            NoteRetenue retenue = NoteRetenue.de(moyenne, rendues.size()); // RG16 (v2)
            note = NoteRetenue.arrondi(retenue.valeur());
            provisoire = retenue.provisoire();
        }
        List<String> commentaires = rendues.stream().map(Relecture::getCommentaire).toList();
        return new ExerciceEtudiantDto(e.getId(), e.getSession().getId(), e.getSession().getTitre(), e.getLien(),
                e.getStatut(), note, provisoire, commentaires);
    }
}
