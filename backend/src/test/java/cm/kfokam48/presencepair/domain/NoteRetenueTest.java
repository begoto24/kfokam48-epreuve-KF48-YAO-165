package cm.kfokam48.presencepair.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Test unitaire de RG16 (v2) : note retenue, provisoire, moyenne d'un étudiant. */
class NoteRetenueTest {

    @Test
    void uneSeuleRelectureRendue_noteProvisoire() {
        assertThat(NoteRetenue.de(14, 1)).isEqualTo(new NoteRetenue(14, true));
    }

    @Test
    void deuxRelecturesRendues_noteDefinitive() {
        assertThat(NoteRetenue.de(16, 2)).isEqualTo(new NoteRetenue(16, false));
    }

    @Test
    void moyenneDesNotesRetenues_arrondieEtProvisoireSiUneLEst() {
        NoteRetenue.Moyenne moyenne = NoteRetenue.moyenne(List.of(
                new NoteRetenue(14, true), new NoteRetenue(16, false), new NoteRetenue(15, true)));
        assertThat(moyenne).isEqualTo(new NoteRetenue.Moyenne(15.0, true));
    }

    @Test
    void toutesDefinitives_moyenneDefinitive() {
        NoteRetenue.Moyenne moyenne = NoteRetenue.moyenne(List.of(new NoteRetenue(11.5, false), new NoteRetenue(15, false)));
        assertThat(moyenne).isEqualTo(new NoteRetenue.Moyenne(13.25, false));
    }

    @Test
    void aucuneNote_moyenneNulleEtNonProvisoire() {
        assertThat(NoteRetenue.moyenne(List.of())).isEqualTo(new NoteRetenue.Moyenne(null, false));
    }

    @Test
    void arrondiADeuxDecimales() {
        assertThat(NoteRetenue.arrondi(44.0 / 3)).isEqualTo(14.67);
        assertThat(NoteRetenue.arrondi(12.345)).isEqualTo(12.35);
    }
}
