package cm.kfokam48.presencepair.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.service.MesExercicesService;
import cm.kfokam48.presencepair.service.RelectureService;
import cm.kfokam48.presencepair.web.dto.ExerciceEtudiantDto;
import cm.kfokam48.presencepair.web.dto.RelectureDto;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final RelectureService relectures;
    private final MesExercicesService mesExercices;

    public EtudiantController(RelectureService relectures, MesExercicesService mesExercices) {
        this.relectures = relectures;
        this.mesExercices = mesExercices;
    }

    /** EF6 : les relectures assignées à l'étudiant. */
    @GetMapping("/{id}/relectures")
    public List<RelectureDto> relectures(@PathVariable Long id) {
        return relectures.assigneesA(id);
    }

    /** EF14 : les exercices de l'étudiant avec leur note retenue, sans les relecteurs (RG8). */
    @GetMapping("/{id}/exercices")
    public List<ExerciceEtudiantDto> exercices(@PathVariable Long id) {
        return mesExercices.deLEtudiant(id);
    }
}
