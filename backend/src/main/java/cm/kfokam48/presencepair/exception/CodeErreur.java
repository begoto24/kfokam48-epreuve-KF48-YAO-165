package cm.kfokam48.presencepair.exception;

import org.springframework.http.HttpStatus;

/**
 * Catalogue des erreurs de l'API : components/schemas/Erreur du contrat.
 * Chaque code a un statut HTTP fixe et un message en français.
 */
public enum CodeErreur {

    CHAMP_MANQUANT(HttpStatus.BAD_REQUEST, "Un champ obligatoire est manquant ou vide."),
    REQUETE_INVALIDE(HttpStatus.BAD_REQUEST, "La requête est mal formée."),
    CODE_INCONNU(HttpStatus.BAD_REQUEST, "Ce code de présence n'existe pas."),
    LIEN_INVALIDE(HttpStatus.BAD_REQUEST, "Le lien doit être une adresse http:// ou https:// de 500 caractères au plus."),
    NOTE_INVALIDE(HttpStatus.BAD_REQUEST, "La note doit être un nombre entier compris entre 0 et 20."),
    PROMOTION_INCONNUE(HttpStatus.NOT_FOUND, "Cette promotion n'existe pas."),
    SESSION_INCONNUE(HttpStatus.NOT_FOUND, "Cette session n'existe pas."),
    ETUDIANT_INCONNU(HttpStatus.NOT_FOUND, "Cet étudiant n'existe pas."),
    EXERCICE_INCONNU(HttpStatus.NOT_FOUND, "Cet exercice n'existe pas."),
    RELECTURE_INCONNUE(HttpStatus.NOT_FOUND, "Cette relecture n'existe pas."),
    AUTO_RELECTURE(HttpStatus.FORBIDDEN, "Un étudiant ne peut pas relire son propre exercice."),
    RELECTEUR_NON_ASSIGNE(HttpStatus.FORBIDDEN, "Cette relecture est assignée à un autre étudiant."),
    DEJA_PRESENT(HttpStatus.CONFLICT, "Ta présence est déjà enregistrée pour cette session."),
    EXERCICE_DEJA_DEPOSE(HttpStatus.CONFLICT, "Un exercice est déjà déposé pour cette session."),
    RELECTURE_DEJA_RENDUE(HttpStatus.CONFLICT, "Cette relecture a déjà été rendue : elle est définitive."),
    RELECTURE_COMMENCEE(HttpStatus.CONFLICT, "La relecture a commencé : le lien ne peut plus être remplacé."),
    SESSION_CLOTUREE(HttpStatus.CONFLICT, "La session est clôturée."),
    SESSION_DEJA_CLOTUREE(HttpStatus.CONFLICT, "La session est déjà clôturée."),
    CODE_EXPIRE(HttpStatus.GONE, "Le code de présence a expiré."),
    TROP_DE_TENTATIVES(HttpStatus.TOO_MANY_REQUESTS, "Trop de codes erronés : réessaie dans deux minutes."),
    ERREUR_INTERNE(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue est survenue.");

    private final HttpStatus statut;
    private final String message;

    CodeErreur(HttpStatus statut, String message) {
        this.statut = statut;
        this.message = message;
    }

    public HttpStatus statut() {
        return statut;
    }

    public String message() {
        return message;
    }
}
