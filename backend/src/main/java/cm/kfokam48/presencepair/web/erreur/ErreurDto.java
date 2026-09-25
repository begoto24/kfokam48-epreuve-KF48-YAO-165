package cm.kfokam48.presencepair.web.erreur;

import cm.kfokam48.presencepair.exception.CodeErreur;

/** Format d'erreur imposé par le contrat, pour toutes les erreurs sans exception. */
public record ErreurDto(String code, String message) {

    public static ErreurDto de(CodeErreur code) {
        return new ErreurDto(code.name(), code.message());
    }
}
