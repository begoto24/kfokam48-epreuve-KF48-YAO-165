package cm.kfokam48.presencepair.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.kfokam48.presencepair.domain.Exercice;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    @Query("select e from Exercice e join fetch e.session where e.etudiant.id = :etudiantId order by e.deposeAt desc")
    List<Exercice> deLAuteur(@Param("etudiantId") Long etudiantId);

    /** RG17 (v2) : exercices de la session qui n'ont pas encore leurs deux relecteurs. */
    @Query("""
            select e from Exercice e
            where e.session.id = :sessionId
              and (select count(r) from Relecture r where r.exercice = e) < :relecteursParExercice
            order by e.deposeAt""")
    List<Exercice> aCompleter(@Param("sessionId") Long sessionId,
            @Param("relecteursParExercice") long relecteursParExercice);
}
