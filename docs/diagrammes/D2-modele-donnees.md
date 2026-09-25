# D2 — Modèle de données

Ce diagramme décrit **exactement** le schéma créé par les migrations Flyway de `backend/src/main/resources/db/migration/` : mêmes tables, mêmes colonnes, mêmes contraintes. Toute migration qui modifie le schéma doit modifier ce fichier dans le même commit.

```mermaid
erDiagram
    PROMOTION ||--o{ ETUDIANT : "regroupe"
    PROMOTION ||--o{ SESSION_COURS : "a pour sessions"
    SESSION_COURS ||--o{ PRESENCE : "enregistre"
    ETUDIANT ||--o{ PRESENCE : "marque"
    ETUDIANT ||--o| TENTATIVE_CODE : "a pour compteur"
    SESSION_COURS ||--o{ EXERCICE : "reçoit"
    ETUDIANT ||--o{ EXERCICE : "dépose (auteur)"
    EXERCICE ||--o| RELECTURE : "est relu par"
    ETUDIANT ||--o{ RELECTURE : "relit (relecteur)"

    PROMOTION {
        BIGINT id PK
        VARCHAR_100 nom UK "NOT NULL"
    }
    ETUDIANT {
        BIGINT id PK
        VARCHAR_100 nom "NOT NULL"
        BIGINT promotion_id FK "NOT NULL"
    }
    SESSION_COURS {
        BIGINT id PK
        VARCHAR_200 titre "NOT NULL"
        BIGINT promotion_id FK "NOT NULL"
        VARCHAR_6 code UK "NOT NULL - RG20"
        TIMESTAMP ouverture_at "NOT NULL"
        TIMESTAMP expiration_at "NOT NULL - ouverture + 15 min (RG1)"
        TIMESTAMP cloturee_at "NULL tant que la session est ouverte"
    }
    PRESENCE {
        BIGINT id PK
        BIGINT session_id FK "NOT NULL"
        BIGINT etudiant_id FK "NOT NULL"
        VARCHAR_10 source "ETUDIANT | FORMATEUR (RG15)"
        TIMESTAMP marquee_at "NOT NULL"
    }
    TENTATIVE_CODE {
        BIGINT etudiant_id PK, FK
        INT echecs_consecutifs "NOT NULL DEFAULT 0 (RG4)"
        TIMESTAMP bloque_jusqu_a "NULL si non bloqué"
    }
    EXERCICE {
        BIGINT id PK
        BIGINT session_id FK "NOT NULL"
        BIGINT etudiant_id FK "NOT NULL - auteur"
        VARCHAR_500 lien "NOT NULL - http(s) (RG18)"
        VARCHAR_25 statut "DEPOSE | EN_ATTENTE_RELECTURE | RELU"
        TIMESTAMP depose_at "NOT NULL"
        TIMESTAMP modifie_at "NULL"
    }
    RELECTURE {
        BIGINT id PK
        BIGINT exercice_id FK, UK "NOT NULL - un seul relecteur (RG6)"
        BIGINT relecteur_id FK "NOT NULL - différent de l'auteur (RG5)"
        INT note "NULL, 0..20 (RG9)"
        VARCHAR_2000 commentaire "NULL"
        VARCHAR_15 statut "A_FAIRE | BROUILLON | RENDUE (RG10)"
        TIMESTAMP assignee_at "NOT NULL"
        TIMESTAMP modifiee_at "NULL"
        TIMESTAMP rendue_at "NULL"
    }
```

## Contraintes d'intégrité

| Contrainte | Table | Règle |
|---|---|---|
| `UNIQUE (session_id, etudiant_id)` | `presence` | RG3 — une présence par étudiant et par session |
| `UNIQUE (session_id, etudiant_id)` | `exercice` | RG13 — un exercice par étudiant et par session |
| `UNIQUE (exercice_id)` | `relecture` | RG6 — un seul relecteur par exercice |
| `UNIQUE (code)` | `session_cours` | RG20 — code unique |
| `CHECK (note BETWEEN 0 AND 20)` | `relecture` | RG9 |
| `CHECK (source IN ('ETUDIANT','FORMATEUR'))` | `presence` | RG15 |
| `CHECK (statut IN (...))` | `exercice`, `relecture` | cycle de vie, voir D4 |

La règle RG5 (relecteur ≠ auteur) porte sur deux tables : elle est garantie par le service d'assignation et vérifiée par un test unitaire, pas par une contrainte SQL.

## Cardinalités

- Une **promotion** regroupe 0..n étudiants et 0..n sessions ; un étudiant appartient à exactement 1 promotion.
- Une **session** a 0..n présences et 0..n exercices.
- Un **étudiant** a au plus 1 présence et au plus 1 exercice **par session**.
- Un **exercice** a 0..1 relecture : 0 tant qu'il est `DEPOSE` (aucun relecteur éligible, RG17), 1 ensuite.
- Un **étudiant** peut être relecteur de 0..n relectures.
- Un **étudiant** a 0..1 compteur de tentatives (créé à sa première erreur de code).
