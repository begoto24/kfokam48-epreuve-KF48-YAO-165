package cm.kfokam48.presencepair.web.dto;

import cm.kfokam48.presencepair.domain.Promotion;

public record PromotionDto(Long id, String nom) {

    public static PromotionDto de(Promotion promotion) {
        return new PromotionDto(promotion.getId(), promotion.getNom());
    }
}
