package cm.kfokam48.presencepair.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/** Test unitaire des règles RG1 et RG2 (B6), sans Spring ni base. */
class SessionCoursTest {

    private static final Instant OUVERTURE = Instant.parse("2026-09-25T08:00:00Z");

    private final SessionCours session = new SessionCours("Titre", new Promotion("P"), "ABCDEF", OUVERTURE);

    @Test
    void rg1_leCodeExpireQuinzeMinutesApresLOuverture() {
        assertThat(session.getExpirationAt()).isEqualTo(Instant.parse("2026-09-25T08:15:00Z"));
    }

    @Test
    void rg1_leCodeEstUtilisableJusquAUneSecondeAvantLExpiration() {
        assertThat(session.codeUtilisableA(OUVERTURE)).isTrue();
        assertThat(session.codeUtilisableA(Instant.parse("2026-09-25T08:14:59Z"))).isTrue();
    }

    @Test
    void rg1_leCodeNEstPlusUtilisableAQuinzeMinutesPile() {
        assertThat(session.codeUtilisableA(Instant.parse("2026-09-25T08:15:00Z"))).isFalse();
        assertThat(session.codeUtilisableA(Instant.parse("2026-09-25T09:00:00Z"))).isFalse();
    }

    @Test
    void rg2_leCodeNEstPlusUtilisableApresLaCloture() {
        session.cloturer(Instant.parse("2026-09-25T08:05:00Z"));
        assertThat(session.codeUtilisableA(Instant.parse("2026-09-25T08:06:00Z"))).isFalse();
    }
}
