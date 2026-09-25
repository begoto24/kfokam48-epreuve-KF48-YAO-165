package cm.kfokam48.presencepair.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.service.ExerciceService;
import cm.kfokam48.presencepair.web.dto.DeposerExerciceRequete;
import cm.kfokam48.presencepair.web.dto.ExerciceDeposeDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

    private final ExerciceService service;

    public ExerciceController(ExerciceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciceDeposeDto deposer(@Valid @RequestBody DeposerExerciceRequete requete) {
        return service.deposer(requete.sessionId(), requete.etudiantId(), requete.lien());
    }
}
