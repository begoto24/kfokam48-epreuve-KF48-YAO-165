package cm.kfokam48.presencepair.web.dto;

import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.StatutRelecture;

/** Une relecture vue par son relecteur : le lien à relire, pas le nom de l'auteur. */
public record RelectureDto(Long id, Long exerciceId, String sessionTitre, String lien, StatutRelecture statut,
        Integer note, String commentaire, boolean sessionCloturee) {

    public static RelectureDto de(Relecture r) {
        return new RelectureDto(r.getId(), r.getExercice().getId(), r.getExercice().getSession().getTitre(),
                r.getExercice().getLien(), r.getStatut(), r.getNote(), r.getCommentaire(),
                r.getExercice().getSession().estCloturee());
    }
}
