package cm.kfokam48.presencepair.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.service.TableauService;
import cm.kfokam48.presencepair.web.dto.LigneTableauDto;

@RestController
@RequestMapping("/api/tableau")
public class TableauController {

    private final TableauService service;

    public TableauController(TableauService service) {
        this.service = service;
    }

    /** Opération imposée : 200, ou 404 PROMOTION_INCONNUE ; sans promotionId, 400 CHAMP_MANQUANT. */
    @GetMapping
    public List<LigneTableauDto> tableau(@RequestParam Long promotionId) {
        return service.tableau(promotionId);
    }
}
