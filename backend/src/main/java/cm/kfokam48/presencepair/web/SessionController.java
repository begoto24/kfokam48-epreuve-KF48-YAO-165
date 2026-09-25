package cm.kfokam48.presencepair.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.service.SessionService;
import cm.kfokam48.presencepair.web.dto.OuvrirSessionRequete;
import cm.kfokam48.presencepair.web.dto.SessionOuverteDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionOuverteDto ouvrir(@Valid @RequestBody OuvrirSessionRequete requete) {
        return service.ouvrir(requete.titre(), requete.promotionId());
    }
}
