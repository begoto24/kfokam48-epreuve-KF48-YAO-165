package cm.kfokam48.presencepair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presencepair.domain.Etudiant;

/**
 * Requêtes agrégées du tableau du formateur (EF8, ENF2) : une requête par colonne,
 * jamais une requête par étudiant. Chaque ligne renvoyée : [etudiantId, valeur].
 */
public interface TableauRepository extends Repository<Etudiant, Long> {

    @Query("""
            select p.etudiant.id, count(p) from Presence p
            where p.session.promotion.id = :promotionId
            group by p.etudiant.id""")
    List<Object[]> presences(@Param("promotionId") Long promotionId);

    @Query("""
            select e.etudiant.id, count(e) from Exercice e
            where e.session.promotion.id = :promotionId
            group by e.etudiant.id""")
    List<Object[]> exercicesDeposes(@Param("promotionId") Long promotionId);

    /** RG16 : seules les relectures RENDUES comptent dans la moyenne. */
    @Query("""
            select e.etudiant.id, avg(r.note) from Relecture r join r.exercice e
            where e.session.promotion.id = :promotionId
              and r.statut = cm.kfokam48.presencepair.domain.StatutRelecture.RENDUE
            group by e.etudiant.id""")
    List<Object[]> moyennes(@Param("promotionId") Long promotionId);

    /** RG11 : relectures pas encore rendues, sessions clôturées comprises. */
    @Query("""
            select r.relecteur.id, count(r) from Relecture r
            where r.relecteur.promotion.id = :promotionId
              and r.statut <> cm.kfokam48.presencepair.domain.StatutRelecture.RENDUE
            group by r.relecteur.id""")
    List<Object[]> relecturesEnAttente(@Param("promotionId") Long promotionId);
}
