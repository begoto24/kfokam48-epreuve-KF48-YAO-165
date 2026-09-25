package cm.kfokam48.presencepair.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/** Test unitaire de RG9 : note entière de 0 à 20, sans Spring. */
class RendreRelectureRequeteTest {

    private static ValidatorFactory fabrique;
    private static Validator validateur;

    @BeforeAll
    static void initialiser() {
        fabrique = Validation.buildDefaultValidatorFactory();
        validateur = fabrique.getValidator();
    }

    @AfterAll
    static void fermer() {
        fabrique.close();
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 10, 20 })
    void rg9_bornesComprises(int note) {
        assertThat(validateur.validate(new RendreRelectureRequete(note, "ok"))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 21, 100 })
    void rg9_horsBornes_noteInvalide(int note) {
        assertThat(messages(new RendreRelectureRequete(note, "ok"))).containsExactly("NOTE_INVALIDE");
    }

    @Test
    void rg9_noteAbsente_noteInvalide() {
        assertThat(messages(new RendreRelectureRequete(null, "ok"))).containsExactly("NOTE_INVALIDE");
    }

    @Test
    void commentaireVide_refuse() {
        assertThat(validateur.validate(new RendreRelectureRequete(12, "  "))).hasSize(1);
    }

    private static java.util.List<String> messages(RendreRelectureRequete requete) {
        return validateur.validate(requete).stream().map(ConstraintViolation::getMessage).toList();
    }
}
