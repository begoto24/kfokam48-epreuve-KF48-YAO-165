package cm.kfokam48.presencepair.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** RG18 : format d'un lien d'exercice. */
class LienTest {

    @ParameterizedTest
    @ValueSource(strings = { "https://github.com/moi/exo", "http://exemple.cm", "HTTPS://gitlab.com/a/b?x=1" })
    void lienHttpValide(String lien) {
        assertThat(Lien.estValide(lien)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   ", "github.com/moi/exo", "ftp://serveur/exo", "https://", "javascript:alert(1)",
            "https://exemple .cm" })
    void lienInvalide(String lien) {
        assertThat(Lien.estValide(lien)).isFalse();
    }

    @Test
    void lienTropLong() {
        assertThat(Lien.estValide("https://exemple.cm/" + "a".repeat(500))).isFalse();
    }
}
