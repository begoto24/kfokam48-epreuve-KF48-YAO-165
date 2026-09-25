# Journal de bord — KF48-YAO-165

Djoukoya-de-begoto Prince Malachie. Une entrée par étape, écrite au moment où je la termine.

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges (15 exigences fonctionnelles, 7 non fonctionnelles, 20 règles de gestion), 4 diagrammes Mermaid (D1 à D3 + D4 bonus), contrat d'API complété (5 opérations imposées intactes + 11 ajoutées), 17 issues priorisées Must/Should/Could et rangées dans les jalons v0.1 et v1.0.

**Bloqué :** environ 30 min sur l'environnement avant de commencer : Java absent du PATH, `winget` indisponible pour installer la GitHub CLI (téléchargement manuel lent). Ensuite, la contradiction Q10/Q15 et le trou sur l'assignation du relecteur. J'ai tranché les deux moi-même : brouillon puis validation définitive, et tirage au dépôt avec file d'attente (section 7).

**IA :** j'ai utilisé Claude pour rédiger le cahier des charges, les diagrammes, le contrat et les issues à partir de mes décisions. Vérifications : j'ai relu chaque RG en face de sa question Qx ; j'ai comparé par script les 5 opérations imposées du contrat original et du mien (mêmes chemins, corps et codes) ; j'ai vérifié que chaque sortie de D3 a son code HTTP dans le contrat, et que chaque colonne de D2 a une règle ou un usage.

---

## Étape 2 — Première version

**Fait :** les 10 issues Must (#1 à #10) et le README (#28), chacune sur sa branche avec sa PR (#18 à #27, #29) qui ferme l'issue. Backend Spring Boot 4 avec 7 tables Flyway identiques à D2, les 5 opérations imposées et 5 opérations de lecture ; 130 tests (unitaires : RG1, RG2, RG5, RG7, RG9, RG16, RG18, RG20 ; intégration : chaque code HTTP des opérations imposées). Frontend React avec les trois écrans. J'ai traité #4 avant #3, parce que l'écran Formateur de #3 avait besoin du squelette React.

**Bloqué :** environ 15 min sur un faux vert : `src/test/resources/application.properties` masquait toute la configuration principale, donc les tests tournaient sans `ddl-auto=validate` ni réglage Jackson. Je l'ai découvert parce que le test RG9 montrait `12.5` tronqué en `12`. Corrigé dans un commit `fix(test)` séparé (profil `test`). Environ 10 min sur les paquets de Spring Boot 4 et de Jackson 3 (MockMvc, exceptions Jackson), que j'ai vérifiés directement dans les jars.

**IA :** Claude a écrit le code issue par issue. Vérifications : un test par règle de gestion et par code HTTP du contrat, puis des appels réels (curl via le proxy Vite) pour chaque scénario d'erreur. Pour le tableau, j'ai calculé à la main les 8 lignes attendues sur les données de démo et je les ai comparées à la réponse de l'API sur une base neuve. J'ai aussi repris une erreur de conception de l'IA : un même code d'erreur doit renvoyer 400 ou 404 selon que l'identifiant est dans le corps ou dans l'URL.

---

## Étape 3 — Enveloppe

**Fait :** avant d'ouvrir l'enveloppe, mon test visuel a trouvé un bug d'interface : choisir une promotion côté Étudiant ne faisait rien (#30, PR #31). Ensuite, environ 1h en deux sujets séparés.
- **Bug** « deux étudiants en même temps, un seul apparaît » : issue #32 d'abord, puis un test rouge commité seul (`2730562`). Sur 20 répétitions, chaque fois une présence est perdue. Cause : deux transactions assignent le même exercice en attente, la contrainte RG6 rejette la seconde et annule aussi sa présence. Corrigé par un verrou sur la session (`dd9f46d`), test vert, PR #33.
- **Changement** « deux relecteurs » : issues #34 et #35, re-priorisation écrite dans #15, #16 et #17. Puis un commit d'analyse dédié (cahier v2, D1, D2, D4), le contrat v1.2 avant le code, une migration V2 ajoutée sans toucher V1, et les PR #36, #37 et #38 (#16 passé Must).

**Bloqué :** environ 10 min sur le test de migration. Une base H2 en mémoire partagée entre Flyway et JDBC renvoyait une erreur de contrainte incompréhensible ; je l'ai isolée par un essai H2 seul, puis remplacée par une base en fichier temporaire. J'ai aussi vérifié la migration V2 sur ma vraie base de développement, remplie pendant le test visuel : aucune donnée perdue.

**IA :** Claude a proposé l'hypothèse de cause du bug. Je ne l'ai acceptée qu'après que le test l'a reproduite (échec à chaque répétition avec `DataIntegrityViolationException`), puis vue disparaître avec le correctif (20/20). Les 4 décisions sur le changement (note provisoire dans la moyenne, un seul pair éligible, données existantes, sacrifice) sont les miennes : l'IA les a proposées, je les ai validées. Les nouvelles moyennes du tableau ont été recalculées à la main dans les tests (15,0 ; 11,5 ; 13,5).

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**
- **EF13 / RG4, blocage après 5 codes faux (#15) → Won't.** C'est une protection contre la devinette, pas le parcours principal, et le code expire de toute façon en 15 min (RG1). Le `429` reste documenté dans le contrat pour plus tard.
- **EF15, détail de session (#17) → Won't.** C'était déjà un Could, et le tableau montre déjà au formateur les relectures en attente (Q11).
- **En échange, EF14 (l'étudiant voit sa note, #16) passe Should → Must**, parce que le client demande que la note provisoire soit « affichée en attendant ».
- Les Should restants (#11 brouillon, #12 remplacer le lien, #13 présence manuelle, #14 clôture) passent après la livraison v1.0 et l'épreuve Git : je ne les ferai que s'il reste du temps.

---

## Étape 4 — Version finale

**Fait :** issue #39. J'ai aligné les documents sur la version du sujet en cinq étapes (plus d'épreuve Git séparée), écrit `CHANGELOG.md` (v0.1 et v1.0, chaque entrée reliée à sa PR et à son issue) et mis le README à jour pour la v1.0. Le README a été **testé depuis un clone vierge** : clone GitHub dans un dossier vide, puis ses 3 commandes. Résultat : migrations V1, V1.1 et V2 appliquées, tableau de démo correct, ouverture de session et présence qui fonctionnent, 144 tests et build frontend verts. J'ai trié le backlog : #14, #13, #11 et #12 reportés après v1.0 dans cet ordre, avec une justification écrite sur chaque issue. Les opérations du contrat non livrées sont marquées comme telles.

**Bloqué :** environ 5 min. Le premier clone, dans un dossier temporaire trop profond, a échoué (« Filename too long », limite de 260 caractères de Windows) ; je l'ai refait dans un chemin court. Le port 5173 étant occupé par mon serveur de développement, Vite a pris le 5174 tout seul.

**IA :** Claude a rédigé le CHANGELOG à partir de la liste réelle des PR fusionnées (`gh pr list`), que j'ai comparée ligne à ligne. Le test du clone vierge est la vérification du README lui-même : je n'ai lancé que ses commandes, rien d'autre.

---

## Étape 5 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
