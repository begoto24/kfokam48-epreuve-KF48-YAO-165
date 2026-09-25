package cm.kfokam48.presencepair.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.repository.PromotionRepository;
import cm.kfokam48.presencepair.web.dto.PromotionDto;

@Service
@Transactional(readOnly = true)
public class PromotionService {

    private final PromotionRepository promotions;

    public PromotionService(PromotionRepository promotions) {
        this.promotions = promotions;
    }

    public List<PromotionDto> lister() {
        return promotions.findAllByOrderByNomAsc().stream().map(PromotionDto::de).toList();
    }
}
