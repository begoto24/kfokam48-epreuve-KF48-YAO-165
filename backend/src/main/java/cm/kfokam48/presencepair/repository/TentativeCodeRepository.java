package cm.kfokam48.presencepair.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cm.kfokam48.presencepair.domain.TentativeCode;

public interface TentativeCodeRepository extends JpaRepository<TentativeCode, Long> {
}
