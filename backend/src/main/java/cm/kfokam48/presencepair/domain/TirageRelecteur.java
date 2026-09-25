package cm.kfokam48.presencepair.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Choix du relecteur d'un exercice, sans accès à la base : testable unitairement.
 * <ul>
 * <li>RG7 : parmi les étudiants présents à la session, au hasard ;</li>
 * <li>RG5 : jamais l'auteur de l'exercice ;</li>
 * <li>RG7 (répartition) : à chance égale, parmi les présents qui ont le moins de relectures dans la session.</li>
 * </ul>
 */
public final class TirageRelecteur {

    private final RandomGenerator aleatoire;

    public TirageRelecteur(RandomGenerator aleatoire) {
        this.aleatoire = aleatoire;
    }

    /**
     * @param auteurId auteur de l'exercice
     * @param presents identifiants des étudiants présents à la session
     * @param charges  nombre de relectures déjà assignées dans la session, par étudiant (absent = 0)
     * @return le relecteur tiré, ou vide si aucun présent n'est éligible (RG17)
     */
    public Optional<Long> choisir(Long auteurId, Collection<Long> presents, Map<Long, Long> charges) {
        List<Long> eligibles = presents.stream().filter(id -> !id.equals(auteurId)).distinct().toList();
        if (eligibles.isEmpty()) {
            return Optional.empty();
        }
        long chargeMin = eligibles.stream().mapToLong(id -> charges.getOrDefault(id, 0L)).min().orElse(0);
        List<Long> moinsCharges = eligibles.stream().filter(id -> charges.getOrDefault(id, 0L) == chargeMin).toList();
        return Optional.of(moinsCharges.get(aleatoire.nextInt(moinsCharges.size())));
    }
}
