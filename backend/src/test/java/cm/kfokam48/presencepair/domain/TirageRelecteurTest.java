package cm.kfokam48.presencepair.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Test unitaire de la règle métier du tirage (B6) : RG5, RG6 (v2), RG7, RG17. */
class TirageRelecteurTest {

    private static final Long AUTEUR = 1L;

    private final TirageRelecteur tirage = new TirageRelecteur(new Random(48));

    @Test
    void rg5_lAuteurNEstJamaisTireSurMilleTirages() {
        for (int i = 0; i < 1000; i++) {
            assertThat(tirage.choisir(AUTEUR, List.of(1L, 2L, 3L), Map.of(), List.of())).get().isNotEqualTo(AUTEUR);
        }
    }

    @Test
    void rg17_seulLAuteurEstPresent_aucunRelecteur() {
        assertThat(tirage.choisir(AUTEUR, List.of(AUTEUR), Map.of(), List.of())).isEmpty();
    }

    @Test
    void rg17_personneNEstPresent_aucunRelecteur() {
        assertThat(tirage.choisir(AUTEUR, List.of(), Map.of(), List.of())).isEmpty();
    }

    @Test
    void rg7_leTirageSeFaitParmiLesMoinsCharges() {
        Map<Long, Long> charges = Map.of(2L, 2L, 3L, 0L, 4L, 1L);
        for (int i = 0; i < 100; i++) {
            assertThat(tirage.choisir(AUTEUR, List.of(1L, 2L, 3L, 4L), charges, List.of())).contains(3L);
        }
    }

    @Test
    void rg6_unRelecteurDejaAssigneNEstJamaisRetire() {
        for (int i = 0; i < 200; i++) {
            assertThat(tirage.choisir(AUTEUR, List.of(1L, 2L, 3L), Map.of(), List.of(2L))).contains(3L);
        }
    }

    @Test
    void rg6_plusAucunCandidatDistinct_aucunRelecteur() {
        assertThat(tirage.choisir(AUTEUR, List.of(1L, 2L), Map.of(), List.of(2L))).isEmpty();
    }

    @Test
    void rg7_aChargeEgaleLeTirageEstAuHasard() {
        Set<Long> tires = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            tires.add(tirage.choisir(AUTEUR, List.of(1L, 2L, 3L, 4L), Map.of(), List.of()).orElseThrow());
        }
        assertThat(tires).containsExactlyInAnyOrder(2L, 3L, 4L);
    }
}
