package cm.kfokam48.presencepair.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.service.PromotionService;
import cm.kfokam48.presencepair.web.dto.PromotionDto;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService service;

    public PromotionController(PromotionService service) {
        this.service = service;
    }

    @GetMapping
    public List<PromotionDto> lister() {
        return service.lister();
    }
}
