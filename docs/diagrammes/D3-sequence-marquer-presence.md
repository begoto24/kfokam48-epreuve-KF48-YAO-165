# D3 — Séquence : marquer sa présence

Opération imposée `POST /api/presences` (contrat, `api/contrat.yaml`). Chaque sortie du diagramme correspond à un code HTTP et à un code d'erreur du contrat.

Ordre des contrôles dans le service : blocage (RG4) → code connu → promotion (RG19) → expiration ou clôture (RG1, RG2) → doublon (RG3).

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant F as Front React<br/>(écran Étudiant)
    participant API as PresenceController
    participant S as PresenceService
    participant T as TentativeCodeRepository
    participant SR as SessionRepository
    participant PR as PresenceRepository
    participant A as AssignationService

    E->>F: choisit son nom, saisit le code
    F->>API: POST /api/presences { code, etudiantId }
    API->>API: validation du corps (code et etudiantId présents)
    alt champ manquant
        API-->>F: 400 { code: "CHAMP_MANQUANT" }
    end
    API->>S: marquer(code, etudiantId)
    S->>T: findByEtudiantId(etudiantId)
    alt étudiant bloqué (RG4)
        S-->>API: EtudiantBloqueException
        API-->>F: 429 { code: "TROP_DE_TENTATIVES" }
    end
    S->>SR: findByCode(code normalisé, RG20)
    alt code inconnu ou session d'une autre promotion (RG19)
        S->>T: echecs_consecutifs + 1 (bloque 2 min au 5e)
        S-->>API: CodeInconnuException
        API-->>F: 400 { code: "CODE_INCONNU" }
    else code expiré ou session clôturée (RG1, RG2)
        S-->>API: CodeExpireException
        API-->>F: 410 { code: "CODE_EXPIRE" }
    else code valide
        S->>PR: existsBySessionIdAndEtudiantId
        alt déjà présent (RG3)
            S-->>API: DejaPresentException
            API-->>F: 409 { code: "DEJA_PRESENT" }
        else cas nominal
            S->>PR: save(Presence, source = ETUDIANT)
            S->>T: remise à zéro du compteur
            S->>A: assignerExercicesEnAttente(session) (RG17)
            S-->>API: PresenceDto
            API-->>F: 201 { id, sessionId, etudiantId, source: "ETUDIANT" }
            F-->>E: « Présence enregistrée »
        end
    end
```

## Correspondance avec le contrat

| Cas | HTTP | `code` d'erreur | Règle |
|---|---|---|---|
| Présence enregistrée | 201 | — | EF3 |
| Champ manquant ou étudiant inconnu | 400 | `CHAMP_MANQUANT`, `ETUDIANT_INCONNU` | — |
| Code inconnu | 400 | `CODE_INCONNU` | RG19, RG4 |
| Déjà présent | 409 | `DEJA_PRESENT` | RG3 |
| Code expiré ou session clôturée | 410 | `CODE_EXPIRE` | RG1, RG2 |
| Trop de tentatives | 429 | `TROP_DE_TENTATIVES` | RG4 |

Le compteur d'échecs est enregistré dans une transaction séparée, pour qu'il ne soit pas annulé par l'exception `CodeInconnuException`.
