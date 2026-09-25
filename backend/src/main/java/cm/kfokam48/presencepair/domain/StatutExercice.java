package cm.kfokam48.presencepair.domain;

/** États du diagramme D4. */
public enum StatutExercice {
    /** Déposé, aucun relecteur éligible pour l'instant (RG17). */
    DEPOSE,
    /** Un relecteur est assigné, la relecture n'est pas rendue (RG11). */
    EN_ATTENTE_RELECTURE,
    /** La relecture est rendue : état final (RG10). */
    RELU
}
