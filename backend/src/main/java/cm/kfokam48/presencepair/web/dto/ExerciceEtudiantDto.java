package cm.kfokam48.presencepair.web.dto;

import java.util.List;

import cm.kfokam48.presencepair.domain.StatutExercice;

/**
 * Un exercice vu par son auteur (EF14, contrat v1.2). Ne contient AUCUNE information
 * sur les relecteurs (RG8) : ni nom, ni identifiant.
 *
 * @param note        note retenue (moyenne des relectures rendues), null si aucune (RG16)
 * @param commentaires commentaires des relectures rendues, dans l'ordre de rendu
 */
public record ExerciceEtudiantDto(Long id, Long sessionId, String sessionTitre, String lien, StatutExercice statut,
        Double note, boolean noteProvisoire, List<String> commentaires) {
}
