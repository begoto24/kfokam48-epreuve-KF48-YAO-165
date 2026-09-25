package cm.kfokam48.presencepair.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import cm.kfokam48.presencepair.domain.Etudiant;
import cm.kfokam48.presencepair.domain.Promotion;
import cm.kfokam48.presencepair.repository.EtudiantRepository;
import cm.kfokam48.presencepair.repository.PromotionRepository;

/** Issue #4 — EF2 : l'étudiant choisit son nom dans la liste de sa promotion. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PromotionControllerIntegrationTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    PromotionRepository promotions;
    @Autowired
    EtudiantRepository etudiants;

    @Test
    void listeLesEtudiantsDeLaPromotionTriesParNom() throws Exception {
        Promotion promotion = promotions.save(new Promotion("Promo EF2"));
        Promotion autre = promotions.save(new Promotion("Autre promo EF2"));
        etudiants.save(new Etudiant("Zoe", promotion));
        etudiants.save(new Etudiant("Alain", promotion));
        etudiants.save(new Etudiant("Intrus", autre));

        mvc.perform(get("/api/promotions/{id}/etudiants", promotion.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Alain"))
                .andExpect(jsonPath("$[1].nom").value("Zoe"))
                .andExpect(jsonPath("$[0].promotionId").value(promotion.getId()));
    }

    @Test
    void promotionInconnue_renvoie404() throws Exception {
        mvc.perform(get("/api/promotions/{id}/etudiants", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }
}
