package cm.kfokam48.presencepair.domain;

import java.net.URI;
import java.net.URISyntaxException;

/** RG18 : un lien d'exercice est une URL absolue http:// ou https:// de 500 caractères au plus. */
public final class Lien {

    public static final int LONGUEUR_MAX = 500;

    private Lien() {
    }

    public static boolean estValide(String lien) {
        if (lien == null || lien.isBlank() || lien.trim().length() > LONGUEUR_MAX) {
            return false;
        }
        try {
            URI uri = new URI(lien.trim());
            String schema = uri.getScheme();
            return ("http".equalsIgnoreCase(schema) || "https".equalsIgnoreCase(schema))
                    && uri.getHost() != null && !uri.getHost().isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
