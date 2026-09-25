package cm.kfokam48.presencepair.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cm.kfokam48.presencepair.domain.Etudiant;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
}
