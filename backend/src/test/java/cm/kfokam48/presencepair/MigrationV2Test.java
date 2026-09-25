package cm.kfokam48.presencepair;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Issue #34 — la migration V2 est appliquée à une base V1 DÉJÀ REMPLIE et doit la faire survivre.
 * Sans Spring : Flyway seul, sur une base H2 dédiée dans un dossier temporaire.
 */
class MigrationV2Test {

    @TempDir
    Path dossier;

    private String url() {
        return "jdbc:h2:" + dossier.resolve("migration_v2").toAbsolutePath();
    }

    @Test
    void baseRemplieEnV1_surviteALaMigrationV2() throws Exception {
        flyway("1").migrate();
        try (Connection c = DriverManager.getConnection(url(), "sa", ""); Statement s = c.createStatement()) {
            s.execute("INSERT INTO promotion (id, nom) VALUES (1, 'P')");
            s.execute("INSERT INTO etudiant (id, nom, promotion_id) VALUES (1, 'A', 1), (2, 'B', 1), (3, 'C', 1)");
            s.execute("INSERT INTO session_cours (id, titre, promotion_id, code, ouverture_at, expiration_at) "
                    + "VALUES (1, 'S', 1, 'MIGR23', LOCALTIMESTAMP, LOCALTIMESTAMP)");
            s.execute("INSERT INTO exercice (id, session_id, etudiant_id, lien, statut, depose_at) VALUES "
                    + "(1, 1, 1, 'https://a', 'RELU', LOCALTIMESTAMP), "
                    + "(2, 1, 2, 'https://b', 'EN_ATTENTE_RELECTURE', LOCALTIMESTAMP)");
            s.execute("INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, statut, assignee_at) VALUES "
                    + "(1, 2, 15, 'ok', 'RENDUE', LOCALTIMESTAMP), (2, 1, NULL, NULL, 'A_FAIRE', LOCALTIMESTAMP)");
        }

        flyway(null).migrate();

        try (Connection c = DriverManager.getConnection(url(), "sa", ""); Statement s = c.createStatement()) {
            assertThat(compter(s, "SELECT COUNT(*) FROM relecture")).isEqualTo(2);
            assertThat(texte(s, "SELECT statut FROM exercice WHERE id = 1")).isEqualTo("PARTIELLEMENT_RELU");
            assertThat(texte(s, "SELECT statut FROM exercice WHERE id = 2")).isEqualTo("EN_ATTENTE_RELECTURE");

            // RG6 v2 : un second relecteur DIFFÉRENT est désormais accepté…
            s.execute("INSERT INTO relecture (exercice_id, relecteur_id, statut, assignee_at) "
                    + "VALUES (1, 3, 'A_FAIRE', LOCALTIMESTAMP)");
            // … mais pas le même relecteur deux fois
            assertThatThrownBy(() -> s.execute("INSERT INTO relecture (exercice_id, relecteur_id, statut, assignee_at) "
                    + "VALUES (1, 2, 'A_FAIRE', LOCALTIMESTAMP)")).hasMessageContaining("UK_RELECTURE_EXERCICE_RELECTEUR");
        }
    }

    private Flyway flyway(String cible) {
        var configuration = Flyway.configure().dataSource(url(), "sa", "").locations("classpath:db/migration");
        if (cible != null) {
            configuration.target(cible);
        }
        return configuration.load();
    }

    private static long compter(Statement s, String sql) throws Exception {
        try (ResultSet r = s.executeQuery(sql)) {
            r.next();
            return r.getLong(1);
        }
    }

    private static String texte(Statement s, String sql) throws Exception {
        try (ResultSet r = s.executeQuery(sql)) {
            r.next();
            return r.getString(1);
        }
    }
}
