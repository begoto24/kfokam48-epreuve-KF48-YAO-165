package cm.kfokam48.presencepair.exception;

import org.springframework.http.HttpStatus;

/**
 * Violation d'une règle de gestion. Levée par les services, traduite en
 * { code, message } par le gestionnaire centralisé (B4).
 */
public class ErreurMetierException extends RuntimeException {

    private final CodeErreur code;
    private final HttpStatus statut;

    public ErreurMetierException(CodeErreur code) {
        this(code, code.statut());
    }

    private ErreurMetierException(CodeErreur code, HttpStatus statut) {
        super(code.message());
        this.code = code;
        this.statut = statut;
    }

    /**
     * Référence inconnue dans le CORPS d'une requête : 400 et non 404
     * (cahier des charges, section 7). Ex. promotionId inconnu dans POST /api/sessions.
     */
    public static ErreurMetierException referenceInvalide(CodeErreur code) {
        return new ErreurMetierException(code, HttpStatus.BAD_REQUEST);
    }

    public CodeErreur getCode() {
        return code;
    }

    public HttpStatus getStatut() {
        return statut;
    }
}
