package cm.kfokam48.presencepair.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Test unitaire de la règle métier du tirage (B6) : RG5, RG7, RG17. */
class TirageRelecteurTest {

    private static final Long AUTEUR = 1L;

    private final TirageRelecteur tirage = new TirageRelecteur(new Random(48));

    @Test
    void rg5_lAuteurNEstJamaisTireSurMilleTirages() {
        for (int i = 0; i < 1000; i++) {
            assertThat(tirage.choisir(AUTEUR, List.of(1L, 2L, 3L), Map.of())).get().isNotEqualTo(AUTEUR);
        }
    }

    @Test
    void rg17_seulLAuteurEstPresent_aucunRelecteur() {
        assertThat(tirage.choisir(AUTEUR, List.of(AUTEUR), Map.of())).isEmpty();
    }

    @Test
    void rg17_personneNEstPresent_aucunRelecteur() {
        assertThat(tirage.choisir(AUTEUR, List.of(), Map.of())).isEmpty();
    }

    @Test
    void rg7_leTirageSeFaitParmiLesMoinsCharges() {
        Map<Long, Long> charges = Map.of(2L, 2L, 3L, 0L, 4L, 1L);
        for (int i = 0; i < 100; i++) {
            assertThat(tirage.choisir(AUTEUR, List.of(1L, 2L, 3L, 4L), charges)).contains(3L);
        }
    }

    @Test
    void rg7_aChargeEgaleLeTirageEstAuHasard() {
        Set<Long> tires = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            tires.add(tirage.choisir(AUTEUR, List.of(1L, 2L, 3L, 4L), Map.of()).orElseThrow());
        }
        assertThat(tires).containsExactlyInAnyOrder(2L, 3L, 4L);
    }
}
