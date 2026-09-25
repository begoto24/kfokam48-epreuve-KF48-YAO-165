package cm.kfokam48.presencepair.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.service.RelectureService;
import cm.kfokam48.presencepair.web.dto.RelectureDto;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final RelectureService relectures;

    public EtudiantController(RelectureService relectures) {
        this.relectures = relectures;
    }

    @GetMapping("/{id}/relectures")
    public List<RelectureDto> relectures(@PathVariable Long id) {
        return relectures.assigneesA(id);
    }
}
