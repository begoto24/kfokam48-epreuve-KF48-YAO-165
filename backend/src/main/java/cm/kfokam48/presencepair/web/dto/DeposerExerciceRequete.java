package cm.kfokam48.presencepair.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Corps imposé de POST /api/exercices. Le format du lien est vérifié par le service (RG18). */
public record DeposerExerciceRequete(@NotNull Long sessionId, @NotNull Long etudiantId, @NotBlank String lien) {
}
