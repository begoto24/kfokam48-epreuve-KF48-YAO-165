package cm.kfokam48.presencepair.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cm.kfokam48.presencepair.domain.Presence;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);
}
