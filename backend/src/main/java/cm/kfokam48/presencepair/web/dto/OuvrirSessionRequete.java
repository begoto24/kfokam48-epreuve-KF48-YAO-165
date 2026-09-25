package cm.kfokam48.presencepair.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corps imposé de POST /api/sessions. */
public record OuvrirSessionRequete(
        @NotBlank @Size(max = 200) String titre,
        @NotNull Long promotionId) {
}
