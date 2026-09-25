# D4 — États-transitions : cycle de vie d'un exercice

> **Version 2 (étape 3)** : conséquence du changement « deux relecteurs par exercice ». Nouvel état `PARTIELLEMENT_RELU` (une relecture rendue sur deux, note retenue **provisoire**), et `RELU` n'est atteint qu'avec deux relectures rendues. La version 1 (un seul relecteur) est dans l'historique Git.

Colonne `exercice.statut` (voir D2). Le statut `DEPOSE` existe à cause du trou identifié en section 7 du cahier des charges : un exercice peut être déposé alors qu'aucun relecteur n'est éligible.

```mermaid
stateDiagram-v2
    [*] --> DEPOSE : POST /api/exercices, aucun présent éligible (RG17)
    [*] --> EN_ATTENTE_RELECTURE : POST /api/exercices, 1 ou 2 relecteurs tirés (RG7)

    DEPOSE --> DEPOSE : PUT lien (RG14)
    DEPOSE --> EN_ATTENTE_RELECTURE : un étudiant éligible devient présent (RG17)

    state EN_ATTENTE_RELECTURE {
        [*] --> UN_RELECTEUR
        UN_RELECTEUR --> DEUX_RELECTEURS : un autre présent arrive (RG17)
        UN_RELECTEUR --> UN_RELECTEUR : PUT lien, si rien commencé (RG14)
        DEUX_RELECTEURS --> DEUX_RELECTEURS : PUT lien, si rien commencé (RG14)
    }

    EN_ATTENTE_RELECTURE --> PARTIELLEMENT_RELU : 1re relecture rendue, note provisoire (RG16)
    PARTIELLEMENT_RELU --> RELU : 2e relecture rendue, note = moyenne des deux (RG16)

    RELU --> [*]

    note right of EN_ATTENTE_RELECTURE
        Les relectures elles-mêmes passent
        A_FAIRE, puis BROUILLON, puis RENDUE (RG10).
        Après la clôture, plus aucune transition :
        l'exercice reste en attente, visible (RG11).
    end note
    note right of PARTIELLEMENT_RELU
        Note retenue affichée, marquée provisoire.
        Si la session est clôturée avant la seconde
        relecture, la note reste provisoire.
    end note
    note right of RELU
        État final : note retenue définitive (RG10, Q15).
    end note
```

## Transitions refusées

| Depuis | Action | Réponse |
|---|---|---|
| une relecture en `BROUILLON` ou `RENDUE` | remplacer le lien | `409 RELECTURE_COMMENCEE` (RG14) |
| `PARTIELLEMENT_RELU`, `RELU` | remplacer le lien | `409 RELECTURE_COMMENCEE` |
| relecture déjà `RENDUE` | la rendre à nouveau | `409 RELECTURE_DEJA_RENDUE` (RG10) |
| tout état, session clôturée | lien, brouillon, relecture | `409 SESSION_CLOTUREE` (RG12, RG11) |
