package cm.kfokam48.presencepair.web.dto;

import java.time.Instant;

import cm.kfokam48.presencepair.domain.SessionCours;

/** Une session sans son code : le code n'est renvoyé qu'à l'ouverture. */
public record SessionDto(Long id, String titre, Long promotionId, Instant ouvertureAt, Instant expirationAt,
        boolean cloturee, Instant clotureeAt) {

    public static SessionDto de(SessionCours s) {
        return new SessionDto(s.getId(), s.getTitre(), s.getPromotion().getId(), s.getOuvertureAt(),
                s.getExpirationAt(), s.estCloturee(), s.getClotureeAt());
    }
}
