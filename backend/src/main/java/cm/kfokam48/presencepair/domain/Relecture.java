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

/** Relecture d'un exercice par un pair. Deux relecteurs différents par exercice (RG6, v2). */
@Entity
@Table(name = "relecture")
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercice_id", nullable = false)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relecteur_id", nullable = false)
    private Etudiant relecteur;

    private Integer note;

    @Column(length = 2000)
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatutRelecture statut;

    @Column(name = "assignee_at", nullable = false)
    private Instant assigneeAt;

    @Column(name = "modifiee_at")
    private Instant modifieeAt;

    @Column(name = "rendue_at")
    private Instant rendueAt;

    protected Relecture() {
    }

    public Relecture(Exercice exercice, Etudiant relecteur, Instant assigneeAt) {
        this.exercice = exercice;
        this.relecteur = relecteur;
        this.assigneeAt = assigneeAt;
        this.statut = StatutRelecture.A_FAIRE;
    }

    public boolean estRendue() {
        return statut == StatutRelecture.RENDUE;
    }

    /** RG14 : une relecture est « commencée » dès qu'un brouillon existe. */
    public boolean estCommencee() {
        return statut != StatutRelecture.A_FAIRE;
    }

    public void enregistrerBrouillon(Integer note, String commentaire, Instant instant) {
        this.note = note;
        this.commentaire = commentaire;
        this.modifieeAt = instant;
        this.statut = StatutRelecture.BROUILLON;
    }

    /** RG10 : la validation est définitive. */
    public void rendre(int note, String commentaire, Instant instant) {
        this.note = note;
        this.commentaire = commentaire;
        this.modifieeAt = instant;
        this.rendueAt = instant;
        this.statut = StatutRelecture.RENDUE;
    }

    public Long getId() {
        return id;
    }

    public Exercice getExercice() {
        return exercice;
    }

    public Etudiant getRelecteur() {
        return relecteur;
    }

    public Integer getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public StatutRelecture getStatut() {
        return statut;
    }

    public Instant getAssigneeAt() {
        return assigneeAt;
    }

    public Instant getModifieeAt() {
        return modifieeAt;
    }

    public Instant getRendueAt() {
        return rendueAt;
    }
}
