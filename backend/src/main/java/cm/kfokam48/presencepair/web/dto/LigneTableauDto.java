package cm.kfokam48.presencepair.web.dto;

/**
 * Ligne de GET /api/tableau. moyenne vaut null si l'étudiant n'a reçu aucune note ;
 * moyenneProvisoire (contrat v1.2) est vrai si une note retenue ne repose que sur une relecture (RG16).
 */
public record LigneTableauDto(Long etudiantId, String nom, long presences, long exercicesDeposes, Double moyenne,
        boolean moyenneProvisoire, long relecturesEnAttente) {
}
