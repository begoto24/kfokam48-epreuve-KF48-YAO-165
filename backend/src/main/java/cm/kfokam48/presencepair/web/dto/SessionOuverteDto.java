package cm.kfokam48.presencepair.web.dto;

import java.time.Instant;

import cm.kfokam48.presencepair.domain.SessionCours;

/** Réponse imposée de POST /api/sessions : seul endroit où le code est renvoyé. */
public record SessionOuverteDto(Long id, String code, Instant ouvertureAt, Instant expirationAt) {

    public static SessionOuverteDto de(SessionCours session) {
        return new SessionOuverteDto(session.getId(), session.getCode(), session.getOuvertureAt(),
                session.getExpirationAt());
    }
}
