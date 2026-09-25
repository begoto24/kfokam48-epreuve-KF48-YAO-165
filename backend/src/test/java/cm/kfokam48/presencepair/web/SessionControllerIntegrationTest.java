package cm.kfokam48.presencepair.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.repository.PromotionRepository;

/** Issue #3 — EF1, RG1, RG20 : POST /api/sessions tel que le contrat l'impose. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SessionControllerIntegrationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    PromotionRepository promotions;

    Long promotionId;

    @BeforeEach
    void promotion() {
        promotionId = promotions.save(new Promotion("Promo EF1")).getId();
    }

    @Test
    void ouvreUneSession_201_avecUnCodeQuiExpireDans15Minutes() throws Exception {
        MvcResult resultat = mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"titre\": \"Spring\", \"promotionId\": " + promotionId + " }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.matchesPattern("[A-HJ-NP-Z2-9]{6}")))
                .andReturn();

        String json = resultat.getResponse().getContentAsString();
        Instant ouverture = Instant.parse(JsonPath.read(json, "$.ouvertureAt"));
        Instant expiration = Instant.parse(JsonPath.read(json, "$.expirationAt"));
        org.assertj.core.api.Assertions.assertThat(expiration).isEqualTo(ouverture.plusSeconds(15 * 60));
    }

    @Test
    void titreVide_400_champManquant() throws Exception {
        mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"titre\": \"  \", \"promotionId\": " + promotionId + " }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CHAMP_MANQUANT"));
    }

    @Test
    void promotionAbsente_400_champManquant() throws Exception {
        mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON).content("{ \"titre\": \"Spring\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CHAMP_MANQUANT"));
    }

    @Test
    void promotionInconnue_400_promotionInconnue() throws Exception {
        mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"titre\": \"Spring\", \"promotionId\": 999999 }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }
}
