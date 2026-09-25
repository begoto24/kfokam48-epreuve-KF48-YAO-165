package cm.kfokam48.presencepair.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/**
 * RG16 (v2) — note retenue d'un exercice : moyenne de ses relectures RENDUES,
 * provisoire tant que moins de deux relectures sont rendues.
 */
public record NoteRetenue(double valeur, boolean provisoire) {

    /**
     * @param moyenneDesRendues moyenne des notes rendues de l'exercice
     * @param rendues           nombre de relectures rendues (au moins 1)
     */
    public static NoteRetenue de(double moyenneDesRendues, long rendues) {
        return new NoteRetenue(moyenneDesRendues, rendues < Exercice.RELECTEURS_PAR_EXERCICE);
    }

    /** Moyenne d'un étudiant : moyenne de ses notes retenues, arrondie, provisoire si l'une l'est. */
    public static Moyenne moyenne(Collection<NoteRetenue> notes) {
        if (notes.isEmpty()) {
            return new Moyenne(null, false);
        }
        double somme = notes.stream().mapToDouble(NoteRetenue::valeur).sum();
        boolean provisoire = notes.stream().anyMatch(NoteRetenue::provisoire);
        return new Moyenne(arrondi(somme / notes.size()), provisoire);
    }

    /** Arrondi à 2 décimales. */
    public static double arrondi(double valeur) {
        return BigDecimal.valueOf(valeur).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /** Moyenne d'un étudiant ; valeur nulle s'il n'a reçu aucune note. */
    public record Moyenne(Double valeur, boolean provisoire) {
    }
}
