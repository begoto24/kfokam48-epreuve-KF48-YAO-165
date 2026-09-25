# Changelog

Toutes les évolutions notables de PrésencePair KFOKAM48. Chaque entrée renvoie à la pull request (PR) et à l'issue correspondantes sur GitHub. Les règles `RGx` et exigences `EFx` renvoient à `docs/CAHIER_DES_CHARGES.md`.

Format inspiré de [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/).

## [1.0] — 25/09/2026 — jalon `[JALON] v1.0`

Version finale : correctif du bug signalé par le client, changement de besoin « deux relecteurs par exercice », livraison.

### Changé (changement de besoin de l'étape 3)
- **Chaque exercice est relu par deux pairs différents**, tirés au sort parmi les présents ; le second est tiré dès qu'un autre présent arrive (RG6 remplace Q6, RG7, RG17) — PR #36, issue #34
- Nouveau statut d'exercice `PARTIELLEMENT_RELU` : une relecture rendue sur deux — PR #36
- **Note retenue** d'un exercice = moyenne de ses relectures rendues, **provisoire** s'il n'y en a qu'une ; le tableau affiche `moyenneProvisoire` (RG16 v2) — PR #37, issue #35
- Contrat d'API **v1.2** : statut `PARTIELLEMENT_RELU`, champ `moyenneProvisoire`, note retenue côté étudiant — commit `docs(api)` de la PR #36
- Migration Flyway **V2** `deux_relecteurs_par_exercice` ajoutée sans modifier V1 ; les exercices déjà relus par un seul pair deviennent `PARTIELLEMENT_RELU` — PR #36
- Cahier des charges v2 et diagrammes D1, D2, D4 mis à jour — commit `docs(analyse)` de la PR #36

### Ajouté
- L'étudiant voit la note retenue de ses exercices (avec la mention provisoire) et les commentaires reçus, jamais ses relecteurs : `GET /api/etudiants/{id}/exercices` (EF14, RG8) — PR #38, issue #16

### Corrigé
- **Deux présences simultanées : une seule était enregistrée** (bug signalé par le client). Deux transactions assignaient le même exercice en attente et la seconde annulait sa présence. Verrou sur la session pendant l'assignation ; reproduit puis vérifié par `PresencesSimultaneesTest` (20 répétitions) — PR #33, issue #32
- Écrans Étudiant et Relecteur : choisir une promotion n'affichait pas la liste des noms (état React périmé) — PR #31, issue #30

### Retiré du périmètre (re-priorisation de l'étape 3)
- Blocage après 5 codes faux (EF13, RG4) — issue #15 passée en *Won't*
- Détail d'une session pour le formateur (EF15) — issue #17 passée en *Won't*

## [0.1] — 25/09/2026 — jalon `[JALON] v0.1`

Première version : toutes les stories **Must** de l'analyse.

### Ajouté
- Socle Spring Boot 4 (Java 17), schéma Flyway V1 conforme au diagramme D2, H2 embarquée, données de démonstration — PR #18, issue #1
- Format d'erreur unique `{ code, message }` pour toutes les erreurs, sans stack trace (B4) — PR #19, issue #2
- Choix de la promotion puis du nom, sans mot de passe (EF2, Q1) ; squelette React avec couche API dédiée et trois onglets — PR #20, issue #4
- Ouverture d'une session avec un code de 6 caractères qui expire à +15 min (EF1, RG1, RG20) — PR #21, issue #3
- Marquage de présence par code : 201, 400 `CODE_INCONNU`, 409 `DEJA_PRESENT`, 410 `CODE_EXPIRE` (EF3) — PR #22, issue #5
- Dépôt du lien d'exercice jusqu'à la clôture (EF4, RG12, RG13, RG18) — PR #23, issue #6
- Tirage au sort du relecteur, jamais l'auteur, avec file d'attente si personne n'est éligible (EF5, RG5, RG7, RG17) — PR #24, issue #7
- Liste des relectures assignées (EF6) — PR #25, issue #8
- Relecture définitive : 400 `NOTE_INVALIDE` (12.5 refusé), 403 `AUTO_RELECTURE`, 409 `RELECTURE_DEJA_RENDUE` (EF7, RG9, RG10) — PR #26, issue #9
- Tableau du formateur calculé par le serveur en requêtes agrégées (EF8, RG16) — PR #27, issue #10
- README de démarrage en trois commandes — PR #29, issue #28

### Corrigé
- Les tests ignoraient `application.properties` (fichier de test homonyme) : profil `test` qui complète la configuration — commit `fix(test)` de la PR #26

## [Analyse] — 25/09/2026 — jalon `[JALON] analyse`

- Cahier des charges v1 (15 EF, 7 ENF, 20 RG, contradiction Q10/Q15 et trou tranchés), diagrammes D1 à D4, contrat d'API v1.1, 17 issues priorisées
