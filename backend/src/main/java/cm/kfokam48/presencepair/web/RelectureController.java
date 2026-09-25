package cm.kfokam48.presencepair.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.service.RelectureService;
import cm.kfokam48.presencepair.web.dto.RelectureDto;
import cm.kfokam48.presencepair.web.dto.RendreRelectureRequete;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/relectures")
public class RelectureController {

    /** En-tête facultatif qui identifie l'étudiant sélectionné (pas d'authentification, Q1). */
    public static final String ENTETE_ETUDIANT = "X-Etudiant-Id";

    private final RelectureService service;

    public RelectureController(RelectureService service) {
        this.service = service;
    }

    /** Opération imposée : 200 quand la relecture est rendue. */
    @PostMapping("/{id}")
    public RelectureDto rendre(@PathVariable Long id, @Valid @RequestBody RendreRelectureRequete requete,
            @RequestHeader(name = ENTETE_ETUDIANT, required = false) Long etudiantCourant) {
        return service.rendre(id, requete.note(), requete.commentaire(), etudiantCourant);
    }
}
