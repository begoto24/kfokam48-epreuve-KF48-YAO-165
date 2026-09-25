package cm.kfokam48.presencepair.web.dto;

import cm.kfokam48.presencepair.domain.Exercice;
import cm.kfokam48.presencepair.domain.StatutExercice;

/** Réponse imposée : { id, statut }. */
public record ExerciceDeposeDto(Long id, StatutExercice statut) {

    public static ExerciceDeposeDto de(Exercice exercice) {
        return new ExerciceDeposeDto(exercice.getId(), exercice.getStatut());
    }
}
