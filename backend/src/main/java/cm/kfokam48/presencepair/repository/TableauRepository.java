package cm.kfokam48.presencepair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presencepair.domain.Etudiant;

/**
 * Requêtes agrégées du tableau du formateur (EF8, ENF2) : une requête par colonne,
 * jamais une requête par étudiant.
 */
public interface TableauRepository extends Repository<Etudiant, Long> {

    /** [etudiantId, nombre de présences]. */
    @Query("""
            select p.etudiant.id, count(p) from Presence p
            where p.session.promotion.id = :promotionId
            group by p.etudiant.id""")
    List<Object[]> presences(@Param("promotionId") Long promotionId);

    /** [etudiantId, nombre d'exercices déposés]. */
    @Query("""
            select e.etudiant.id, count(e) from Exercice e
            where e.session.promotion.id = :promotionId
            group by e.etudiant.id""")
    List<Object[]> exercicesDeposes(@Param("promotionId") Long promotionId);

    /**
     * RG16 (v2) : une ligne par exercice ayant au moins une relecture RENDUE :
     * [auteurId, moyenne des notes rendues, nombre de relectures rendues].
     */
    @Query("""
            select e.etudiant.id, avg(r.note), count(r) from Relecture r join r.exercice e
            where e.session.promotion.id = :promotionId
              and r.statut = cm.kfokam48.presencepair.domain.StatutRelecture.RENDUE
            group by e.etudiant.id, e.id""")
    List<Object[]> notesRetenuesParExercice(@Param("promotionId") Long promotionId);

    /** RG11 : [relecteurId, relectures pas encore rendues], sessions clôturées comprises. */
    @Query("""
            select r.relecteur.id, count(r) from Relecture r
            where r.relecteur.promotion.id = :promotionId
              and r.statut <> cm.kfokam48.presencepair.domain.StatutRelecture.RENDUE
            group by r.relecteur.id""")
    List<Object[]> relecturesEnAttente(@Param("promotionId") Long promotionId);
}
