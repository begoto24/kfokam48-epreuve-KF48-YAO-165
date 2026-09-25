package cm.kfokam48.presencepair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presencepair.domain.Relecture;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /** Nombre de relectures par relecteur dans une session : [relecteurId, nombre]. */
    @Query("""
            select r.relecteur.id, count(r) from Relecture r
            where r.exercice.session.id = :sessionId
            group by r.relecteur.id""")
    List<Object[]> chargesParRelecteur(@Param("sessionId") Long sessionId);
}
