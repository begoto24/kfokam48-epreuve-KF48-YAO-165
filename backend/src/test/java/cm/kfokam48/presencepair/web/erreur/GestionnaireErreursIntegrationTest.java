package cm.kfokam48.presencepair.web.erreur;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Issue #2 — ENF4, B4 : toute erreur renvoie { code, message } et jamais de stack trace. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(GestionnaireErreursIntegrationTest.ControleurDeTest.class)
class GestionnaireErreursIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void erreurMetier_renvoieSonStatutEtSonCode() throws Exception {
        formatErreur(mvc.perform(get("/test-erreurs/metier")), "CODE_EXPIRE").andExpect(status().isGone());
    }

    @Test
    void referenceInconnueDansLeCorps_renvoie400() throws Exception {
        formatErreur(mvc.perform(get("/test-erreurs/reference")), "PROMOTION_INCONNUE")
                .andExpect(status().isBadRequest());
    }

    @Test
    void jsonMalForme_renvoieRequeteInvalide() throws Exception {
        formatErreur(mvc.perform(post("/test-erreurs/corps").contentType(MediaType.APPLICATION_JSON).content("{ pas du json")),
                "REQUETE_INVALIDE").andExpect(status().isBadRequest());
    }

    @Test
    void champManquant_renvoieChampManquant() throws Exception {
        formatErreur(mvc.perform(post("/test-erreurs/corps").contentType(MediaType.APPLICATION_JSON).content("{ \"note\": 12 }")),
                "CHAMP_MANQUANT").andExpect(status().isBadRequest());
    }

    @Test
    void contrainteAvecCode_renvoieCeCode() throws Exception {
        formatErreur(mvc.perform(post("/test-erreurs/corps").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"titre\": \"t\", \"note\": 21 }")), "NOTE_INVALIDE").andExpect(status().isBadRequest());
    }

    @Test
    void erreurImprevue_renvoie500SansTrace() throws Exception {
        formatErreur(mvc.perform(get("/test-erreurs/imprevue")), "ERREUR_INTERNE")
                .andExpect(status().isInternalServerError());
    }

    @Test
    void routeInconnue_renvoie404AuFormatImpose() throws Exception {
        formatErreur(mvc.perform(get("/api/n-existe-pas")), "REQUETE_INVALIDE").andExpect(status().isNotFound());
    }

    private static ResultActions formatErreur(ResultActions resultat, String code) throws Exception {
        return resultat
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());
    }

    record Corps(@NotBlank String titre, @NotNull @Min(value = 0, message = "NOTE_INVALIDE")
            @Max(value = 20, message = "NOTE_INVALIDE") Integer note) {
    }

    @RestController
    @RequestMapping("/test-erreurs")
    static class ControleurDeTest {

        @GetMapping("/metier")
        void metier() {
            throw new ErreurMetierException(CodeErreur.CODE_EXPIRE);
        }

        @GetMapping("/reference")
        void reference() {
            throw ErreurMetierException.referenceInvalide(CodeErreur.PROMOTION_INCONNUE);
        }

        @PostMapping("/corps")
        void corps(@Valid @RequestBody Corps corps) {
        }

        @GetMapping("/imprevue")
        void imprevue() {
            throw new IllegalStateException("détail interne à ne pas divulguer");
        }
    }
}
