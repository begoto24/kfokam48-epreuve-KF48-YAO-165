package cm.kfokam48.presencepair.domain;

/** États du diagramme D4. */
public enum StatutExercice {
    /** Déposé, aucun relecteur éligible pour l'instant (RG17). */
    DEPOSE,
    /** Au moins un relecteur est assigné, aucune relecture n'est rendue (RG11). */
    EN_ATTENTE_RELECTURE,
    /** Une relecture rendue sur deux : note retenue provisoire (RG16, v2). */
    PARTIELLEMENT_RELU,
    /** Les deux relectures sont rendues : état final (RG10, RG16). */
    RELU
}
