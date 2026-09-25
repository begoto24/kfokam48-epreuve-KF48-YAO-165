# D4 — États-transitions : cycle de vie d'un exercice

Colonne `exercice.statut` (voir D2). Le statut `DEPOSE` existe à cause du trou identifié en section 7 du cahier des charges : un exercice peut être déposé alors qu'aucun relecteur n'est éligible.

```mermaid
stateDiagram-v2
    [*] --> DEPOSE : POST /api/exercices, aucun présent éligible (RG17)
    [*] --> EN_ATTENTE_RELECTURE : POST /api/exercices, relecteur tiré au sort (RG7)

    DEPOSE --> DEPOSE : PUT lien (RG14)
    DEPOSE --> EN_ATTENTE_RELECTURE : un étudiant éligible devient présent (RG17)

    state EN_ATTENTE_RELECTURE {
        [*] --> A_FAIRE
        A_FAIRE --> A_FAIRE : PUT lien (RG14)
        A_FAIRE --> BROUILLON : PUT brouillon
        BROUILLON --> BROUILLON : PUT brouillon (RG10)
    }

    EN_ATTENTE_RELECTURE --> RELU : POST /api/relectures/:id, note 0..20 (RG9)

    RELU --> [*]

    note right of EN_ATTENTE_RELECTURE
        Les sous-états sont ceux de relecture.statut.
        Après la clôture de la session, plus aucune
        transition n'est possible : l'exercice reste
        « en attente » et le formateur le voit (RG11).
    end note
    note right of RELU
        État final : la note est définitive (RG10, Q15).
    end note
```

## Transitions refusées

| Depuis | Action | Réponse |
|---|---|---|
| `EN_ATTENTE_RELECTURE / BROUILLON` | remplacer le lien | `409 RELECTURE_COMMENCEE` (RG14) |
| `RELU` | remplacer le lien | `409 RELECTURE_COMMENCEE` |
| `RELU` | rendre ou modifier la relecture | `409 RELECTURE_DEJA_RENDUE` (RG10) |
| tout état, session clôturée | lien, brouillon, relecture | `409 SESSION_CLOTUREE` (RG12, RG11) |
