# PrésencePair KFOKAM48

Application de présence par code et de relecture entre pairs pour la formation KFOKAM48 : un formateur ouvre une session et obtient un code de présence ; les étudiants marquent leur présence, déposent le lien de leur exercice, et chaque exercice est relu par **deux pairs tirés au sort** ; la note retenue est la moyenne des deux, **provisoire** tant qu'une seule relecture est rendue ; le formateur suit tout dans un tableau.

Version **1.0** — voir [CHANGELOG.md](CHANGELOG.md).

Épreuve finale fullstack — Djoukoya-de-begoto Prince Malachie · KF48-YAO-165

**Frontend : React** (Vite + TypeScript), parce que c'est le framework que je maîtrise le mieux et que trois écrans simples ne justifient ni la structure d'Angular ni le rendu serveur de Next.js.

## Prérequis

- Java 17 ou plus (`java -version`)
- Node.js 18 ou plus (`node --version`)

Rien d'autre : la base H2 est embarquée, Maven est téléchargé par le wrapper `mvnw`.

## Démarrer (3 commandes, deux terminaux)

Depuis la racine du dépôt :

| | Linux / macOS | Windows (PowerShell) |
|---|---|---|
| 1. Backend (port 8080) | `cd backend && ./mvnw spring-boot:run` | `cd backend; .\mvnw.cmd spring-boot:run` |
| 2. Dépendances du frontend (dans un second terminal) | `cd frontend && npm install` | `cd frontend; npm install` |
| 3. Frontend (port 5173) | `npm run dev` | `npm run dev` |

Puis ouvrir **http://localhost:5173**. Le frontend relaie `/api` vers le backend.

Le premier démarrage du backend télécharge Maven et les dépendances : compter quelques minutes.

## Données de démonstration

Chargées automatiquement au premier démarrage (`backend/src/main/resources/db/demo/`) :

- **KFOKAM48 - Promotion 2026** : 8 étudiants, 2 sessions
  - *Spring Boot - les bases* (clôturée) : relectures rendues, une relecture jamais faite, un brouillon, une présence ajoutée par le formateur ;
  - *React - composants et etat* (code expiré, session ouverte) : dépôts et relectures en cours.
- **KFOKAM48 - Cours du soir** : 3 étudiants, 1 session avec un exercice sans relecteur éligible.

Au démarrage, la migration V2 transforme les exercices de démo déjà notés en **notes provisoires** (une seule relecture sur les deux attendues) : c'est visible dans le tableau et dans *Mes exercices et mes notes*.

Les codes des sessions de démo sont expirés. **Pour tester le marquage de présence**, ouvrir une nouvelle session dans l'onglet *Formateur*, puis saisir le code affiché dans l'onglet *Étudiant*.

Pour repartir de données neuves : arrêter le backend et supprimer le dossier `backend/data/`.

## Parcours rapide

1. **Formateur** : choisir *KFOKAM48 - Promotion 2026*, ouvrir une session → un code s'affiche ; le tableau est en dessous.
2. **Étudiant** : choisir la promotion puis un nom (pas de mot de passe), par exemple *Bello Ibrahim* ; saisir le code, puis déposer un lien dans la nouvelle session. Faire de même avec *Kamga Sandrine* et *Mbarga Yannick* (présence seulement) : l'exercice de Bello reçoit ses **deux relecteurs**.
3. **Relecteur** : choisir *Kamga Sandrine*, rendre une note (essayer d'abord `12.5` ou `21` pour voir l'erreur) ; la note de Bello est alors **provisoire**. Rendre la seconde avec *Mbarga Yannick* : elle devient définitive.
4. **Étudiant** *Bello Ibrahim* → *Mes exercices et mes notes* ; **Formateur** → *Actualiser* le tableau.

## Tests

```bash
cd backend && ./mvnw verify      # tests unitaires et d'intégration, base H2 en mémoire
cd frontend && npm run build     # vérification TypeScript et build de production
```

Les tests n'utilisent ni la base locale ni les données de démonstration.

## Structure

```
docs/       CAHIER_DES_CHARGES.md · JOURNAL.md · diagrammes/ (Mermaid)
api/        contrat.yaml (OpenAPI : 5 opérations imposées + opérations ajoutées)
backend/    Spring Boot 4 (Java 17), Flyway, H2 — web → service → repository, DTO
frontend/   React 19 + Vite + TypeScript — src/api/ est la seule couche d'appels HTTP
```
