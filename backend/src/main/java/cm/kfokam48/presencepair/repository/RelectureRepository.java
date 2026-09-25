package cm.kfokam48.presencepair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presencepair.domain.Relecture;
import cm.kfokam48.presencepair.domain.StatutRelecture;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /** Nombre de relectures par relecteur dans une session : [relecteurId, nombre]. */
    @Query("""
            select r.relecteur.id, count(r) from Relecture r
            where r.exercice.session.id = :sessionId
            group by r.relecteur.id""")
    List<Object[]> chargesParRelecteur(@Param("sessionId") Long sessionId);

    @Query("select r.relecteur.id from Relecture r where r.exercice.id = :exerciceId")
    List<Long> relecteursDe(@Param("exerciceId") Long exerciceId);

    long countByExerciceIdAndStatut(Long exerciceId, StatutRelecture statut);

    /** EF6 : les relectures d'un étudiant, celles à faire d'abord, les plus récentes en tête. */
    @Query("""
            select r from Relecture r
            join fetch r.exercice e
            join fetch e.session
            where r.relecteur.id = :relecteurId
            order by case when r.statut = cm.kfokam48.presencepair.domain.StatutRelecture.RENDUE then 1 else 0 end,
                     r.assigneeAt desc""")
    List<Relecture> assigneesA(@Param("relecteurId") Long relecteurId);
}
