package cm.kfokam48.presencepair.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corps imposé de POST /api/relectures/{id}. RG9 : note entière de 0 à 20. */
public record RendreRelectureRequete(
        @NotNull(message = "NOTE_INVALIDE") @Min(value = 0, message = "NOTE_INVALIDE")
        @Max(value = 20, message = "NOTE_INVALIDE") Integer note,
        @NotBlank @Size(max = 2000) String commentaire) {
}
