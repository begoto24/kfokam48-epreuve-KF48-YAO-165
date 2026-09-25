package cm.kfokam48.presencepair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cm.kfokam48.presencepair.domain.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findAllByOrderByNomAsc();
}
