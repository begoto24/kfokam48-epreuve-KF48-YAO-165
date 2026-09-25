package cm.kfokam48.presencepair.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Corps imposé de POST /api/presences. */
public record MarquerPresenceRequete(@NotBlank String code, @NotNull Long etudiantId) {
}
