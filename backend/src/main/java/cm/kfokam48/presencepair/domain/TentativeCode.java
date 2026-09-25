package cm.kfokam48.presencepair.domain;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Compteur de codes faux d'un étudiant (RG4). */
@Entity
@Table(name = "tentative_code")
public class TentativeCode {

    public static final int ECHECS_MAX = 5;
    public static final Duration DUREE_BLOCAGE = Duration.ofMinutes(2);

    @Id
    @Column(name = "etudiant_id")
    private Long etudiantId;

    @Column(name = "echecs_consecutifs", nullable = false)
    private int echecsConsecutifs;

    @Column(name = "bloque_jusqu_a")
    private Instant bloqueJusquA;

    protected TentativeCode() {
    }

    public TentativeCode(Long etudiantId) {
        this.etudiantId = etudiantId;
    }

    public boolean estBloqueA(Instant instant) {
        return bloqueJusquA != null && instant.isBefore(bloqueJusquA);
    }

    /** Enregistre un code faux ; au 5e échec consécutif, bloque l'étudiant 2 minutes. */
    public void enregistrerEchec(Instant instant) {
        if (bloqueJusquA != null && !instant.isBefore(bloqueJusquA)) {
            reinitialiser(); // le blocage précédent est terminé : le compteur repart de zéro
        }
        echecsConsecutifs++;
        if (echecsConsecutifs >= ECHECS_MAX) {
            bloqueJusquA = instant.plus(DUREE_BLOCAGE);
        }
    }

    public void reinitialiser() {
        echecsConsecutifs = 0;
        bloqueJusquA = null;
    }

    public Long getEtudiantId() {
        return etudiantId;
    }

    public int getEchecsConsecutifs() {
        return echecsConsecutifs;
    }

    public Instant getBloqueJusquA() {
        return bloqueJusquA;
    }
}
