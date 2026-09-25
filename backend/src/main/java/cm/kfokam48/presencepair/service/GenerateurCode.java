package cm.kfokam48.presencepair.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

/**
 * RG20 : code de 6 caractères, majuscules et chiffres, sans caractères ambigus
 * (0/O, 1/I) pour qu'il se recopie sans erreur depuis le tableau de la salle.
 */
@Component
public class GenerateurCode {

    public static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    public static final int LONGUEUR = 6;

    private final SecureRandom aleatoire = new SecureRandom();

    public String generer() {
        StringBuilder code = new StringBuilder(LONGUEUR);
        for (int i = 0; i < LONGUEUR; i++) {
            code.append(ALPHABET.charAt(aleatoire.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }

    /** Saisie insensible à la casse et aux espaces (RG20). */
    public static String normaliser(String saisie) {
        return saisie == null ? "" : saisie.replaceAll("\s", "").toUpperCase();
    }
}
