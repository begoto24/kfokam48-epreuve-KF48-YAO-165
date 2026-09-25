package cm.kfokam48.presencepair.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Exercice déposé par un étudiant. Cycle de vie : docs/diagrammes/D4-etats-exercice.md. */
@Entity
@Table(name = "exercice")
public class Exercice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    /** L'auteur de l'exercice. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Column(nullable = false, length = 500)
    private String lien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatutExercice statut;

    @Column(name = "depose_at", nullable = false)
    private Instant deposeAt;

    @Column(name = "modifie_at")
    private Instant modifieAt;

    protected Exercice() {
    }

    public Exercice(SessionCours session, Etudiant etudiant, String lien, Instant deposeAt) {
        this.session = session;
        this.etudiant = etudiant;
        this.lien = lien;
        this.deposeAt = deposeAt;
        this.statut = StatutExercice.DEPOSE;
    }

    public void marquerEnAttenteDeRelecture() {
        this.statut = StatutExercice.EN_ATTENTE_RELECTURE;
    }

    public void marquerRelu() {
        this.statut = StatutExercice.RELU;
    }

    public void remplacerLien(String nouveauLien, Instant instant) {
        this.lien = nouveauLien;
        this.modifieAt = instant;
    }

    public Long getId() {
        return id;
    }

    public SessionCours getSession() {
        return session;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public String getLien() {
        return lien;
    }

    public StatutExercice getStatut() {
        return statut;
    }

    public Instant getDeposeAt() {
        return deposeAt;
    }

    public Instant getModifieAt() {
        return modifieAt;
    }
}
