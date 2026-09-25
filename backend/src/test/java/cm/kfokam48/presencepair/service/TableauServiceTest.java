package cm.kfokam48.presencepair.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** RG16 : arrondi de la moyenne à 2 décimales, null sans note. */
class TableauServiceTest {

    @Test
    void rg16_arrondiADeuxDecimales() {
        assertThat(TableauService.arrondi(44.0 / 3)).isEqualTo(14.67);
        assertThat(TableauService.arrondi(12.345)).isEqualTo(12.35);
        assertThat(TableauService.arrondi(15)).isEqualTo(15.0);
    }

    @Test
    void rg16_aucuneNote_moyenneNulle() {
        assertThat(TableauService.arrondi(null)).isNull();
    }
}
