package cm.kfokam48.presencepair.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.NoteRetenue;
import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.repository.TableauRepository;
import cm.kfokam48.presencepair.web.dto.LigneTableauDto;

/** EF8 : le tableau du formateur. La moyenne est calculée ici et nulle part ailleurs (RG16, F3). */
@Service
@Transactional(readOnly = true)
public class TableauService {

    private final PromotionRepository promotions;
    private final EtudiantRepository etudiants;
    private final TableauRepository tableau;

    public TableauService(PromotionRepository promotions, EtudiantRepository etudiants, TableauRepository tableau) {
        this.promotions = promotions;
        this.etudiants = etudiants;
        this.tableau = tableau;
    }

    public List<LigneTableauDto> tableau(Long promotionId) {
        if (!promotions.existsById(promotionId)) {
            throw new ErreurMetierException(CodeErreur.PROMOTION_INCONNUE);
        }
        Map<Long, Number> presences = parEtudiant(tableau.presences(promotionId));
        Map<Long, Number> exercices = parEtudiant(tableau.exercicesDeposes(promotionId));
        Map<Long, List<NoteRetenue>> notes = notesRetenues(tableau.notesRetenuesParExercice(promotionId));
        Map<Long, Number> enAttente = parEtudiant(tableau.relecturesEnAttente(promotionId));

        return etudiants.findByPromotionIdOrderByNomAsc(promotionId).stream()
                .map(e -> {
                    NoteRetenue.Moyenne moyenne = NoteRetenue.moyenne(notes.getOrDefault(e.getId(), List.of()));
                    return new LigneTableauDto(e.getId(), e.getNom(),
                            compte(presences, e.getId()),
                            compte(exercices, e.getId()),
                            moyenne.valeur(),
                            moyenne.provisoire(),
                            compte(enAttente, e.getId()));
                })
                .toList();
    }

    /** RG16 (v2) : note retenue de chaque exercice, regroupée par auteur. */
    private static Map<Long, List<NoteRetenue>> notesRetenues(List<Object[]> lignes) {
        Map<Long, List<NoteRetenue>> resultat = new HashMap<>();
        for (Object[] ligne : lignes) {
            NoteRetenue note = NoteRetenue.de(((Number) ligne[1]).doubleValue(), ((Number) ligne[2]).longValue());
            resultat.computeIfAbsent((Long) ligne[0], id -> new ArrayList<>()).add(note);
        }
        return resultat;
    }

    private static long compte(Map<Long, Number> valeurs, Long etudiantId) {
        Number valeur = valeurs.get(etudiantId);
        return valeur == null ? 0 : valeur.longValue();
    }

    private static Map<Long, Number> parEtudiant(List<Object[]> lignes) {
        Map<Long, Number> resultat = new HashMap<>();
        for (Object[] ligne : lignes) {
            resultat.put((Long) ligne[0], (Number) ligne[1]);
        }
        return resultat;
    }
}
