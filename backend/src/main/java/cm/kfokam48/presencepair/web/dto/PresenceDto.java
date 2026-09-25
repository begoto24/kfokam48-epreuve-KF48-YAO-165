package cm.kfokam48.presencepair.web.dto;

import cm.kfokam48.presencepair.domain.Presence;
import cm.kfokam48.presencepair.domain.SourcePresence;

/** Réponse imposée : { id, sessionId, etudiantId, source }. */
public record PresenceDto(Long id, Long sessionId, Long etudiantId, SourcePresence source) {

    public static PresenceDto de(Presence presence) {
        return new PresenceDto(presence.getId(), presence.getSession().getId(), presence.getEtudiant().getId(),
                presence.getSource());
    }
}
