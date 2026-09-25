package cm.kfokam48.presencepair.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cm.kfokam48.presencepair.domain.SessionCours;

public interface SessionCoursRepository extends JpaRepository<SessionCours, Long> {

    boolean existsByCode(String code);

    Optional<SessionCours> findByCode(String code);
}
