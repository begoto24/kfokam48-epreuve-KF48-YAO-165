package cm.kfokam48.presencepair.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import cm.kfokam48.presencepair.domain.SessionCours;

public interface SessionCoursRepository extends JpaRepository<SessionCours, Long> {

    boolean existsByCode(String code);

    Optional<SessionCours> findByCode(String code);

    List<SessionCours> findByPromotionIdOrderByOuvertureAtDesc(Long promotionId);

    /**
     * Verrou d'écriture sur la session (SELECT ... FOR UPDATE) : les assignations d'une même
     * session s'exécutent l'une après l'autre (bug #32).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SessionCours s where s.id = :id")
    Optional<SessionCours> verrouiller(@Param("id") Long id);
}
