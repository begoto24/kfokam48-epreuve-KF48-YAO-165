package cm.kfokam48.presencepair.web.erreur;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cm.kfokam48.presencepair.exception.CodeErreur;
import cm.kfokam48.presencepair.exception.ErreurMetierException;
import tools.jackson.core.JacksonException;

/**
 * Gestion centralisée des erreurs (B4) : toute erreur devient { code, message },
 * jamais de stack trace, de corps vide ni de page d'erreur Spring.
 */
@RestControllerAdvice
public class GestionnaireErreurs {

    private static final Logger LOG = LoggerFactory.getLogger(GestionnaireErreurs.class);

    @ExceptionHandler(ErreurMetierException.class)
    ResponseEntity<ErreurDto> metier(ErreurMetierException e) {
        return repondre(e.getStatut(), e.getCode());
    }

    /**
     * Bean Validation : le message de la contrainte porte le code d'erreur
     * (ex. @Min(value = 0, message = "NOTE_INVALIDE")). Par défaut : CHAMP_MANQUANT.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErreurDto> validation(MethodArgumentNotValidException e) {
        FieldError erreur = e.getBindingResult().getFieldError();
        CodeErreur code = erreur == null ? CodeErreur.CHAMP_MANQUANT : codeDepuis(erreur.getDefaultMessage());
        return repondre(HttpStatus.BAD_REQUEST, code);
    }

    /** JSON mal formé ou type incorrect. Une note non entière (12.5) donne NOTE_INVALIDE (RG9). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErreurDto> illisible(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof JacksonException jackson
                && jackson.getPath().stream().anyMatch(ref -> "note".equals(ref.getPropertyName()))) {
            return repondre(HttpStatus.BAD_REQUEST, CodeErreur.NOTE_INVALIDE);
        }
        return repondre(HttpStatus.BAD_REQUEST, CodeErreur.REQUETE_INVALIDE);
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class, MissingRequestHeaderException.class })
    ResponseEntity<ErreurDto> parametreManquant(Exception e) {
        return repondre(HttpStatus.BAD_REQUEST, CodeErreur.CHAMP_MANQUANT);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, HttpMediaTypeNotSupportedException.class })
    ResponseEntity<ErreurDto> requeteInvalide(Exception e) {
        return repondre(HttpStatus.BAD_REQUEST, CodeErreur.REQUETE_INVALIDE);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErreurDto> routeInconnue(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErreurDto(CodeErreur.REQUETE_INVALIDE.name(), "Cette adresse n'existe pas dans l'API."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErreurDto> verbeInvalide(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErreurDto(CodeErreur.REQUETE_INVALIDE.name(), "Cette méthode HTTP n'est pas acceptée ici."));
    }

    /** Filet de sécurité : l'erreur est journalisée côté serveur, le client ne voit rien d'interne. */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErreurDto> imprevue(Exception e) {
        LOG.error("Erreur imprévue", e);
        return repondre(HttpStatus.INTERNAL_SERVER_ERROR, CodeErreur.ERREUR_INTERNE);
    }

    private static CodeErreur codeDepuis(String message) {
        return Arrays.stream(CodeErreur.values())
                .filter(c -> c.name().equals(message))
                .findFirst()
                .orElse(CodeErreur.CHAMP_MANQUANT);
    }

    private static ResponseEntity<ErreurDto> repondre(HttpStatus statut, CodeErreur code) {
        return ResponseEntity.status(statut).body(ErreurDto.de(code));
    }
}
