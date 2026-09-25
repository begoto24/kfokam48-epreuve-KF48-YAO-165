package cm.kfokam48.presencepair.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/** RG20 : format du code et normalisation de la saisie. */
class GenerateurCodeTest {

    private final GenerateurCode generateur = new GenerateurCode();

    @RepeatedTest(50)
    void rg20_sixCaracteresSansCaractereAmbigu() {
        assertThat(generateur.generer()).matches("[A-HJ-NP-Z2-9]{6}");
    }

    @Test
    void rg20_saisieInsensibleALaCasseEtAuxEspaces() {
        assertThat(GenerateurCode.normaliser(" ab c2d3 ")).isEqualTo("ABC2D3");
    }
}
