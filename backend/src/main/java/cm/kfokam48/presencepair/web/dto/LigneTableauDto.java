package cm.kfokam48.presencepair.web.dto;

/** Ligne imposée de GET /api/tableau. moyenne vaut null si l'étudiant n'a reçu aucune note (RG16). */
public record LigneTableauDto(Long etudiantId, String nom, long presences, long exercicesDeposes, Double moyenne,
        long relecturesEnAttente) {
}
