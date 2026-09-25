# Cahier des charges — PrésencePair KFOKAM48

**Auteur :** Djoukoya-de-begoto Prince Malachie · KF48-YAO-165
**Version :** 2 (changement de besoin de l'étape 3 : deux relecteurs par exercice) · **Date :** 25/09/2026
**Frontend choisi :** React (Vite + TypeScript), parce que c'est le framework que je maîtrise le mieux et que trois écrans simples ne justifient pas la structure plus lourde d'Angular ou le rendu serveur de Next.js.

---

## 1. Contexte et objectif

La direction de la formation KFOKAM48 suit aujourd'hui la présence et les exercices de ses étudiants à la main. Deux problèmes en découlent : la présence n'est pas fiable (un étudiant peut se déclarer présent sans l'être) et les exercices déposés ne sont pas relus de façon organisée.

L'application **PrésencePair** répond à ces deux problèmes :

- le formateur ouvre une session de cours et obtient un **code de présence éphémère** que seuls les étudiants dans la salle connaissent ;
- chaque étudiant dépose le **lien** de son exercice, qui est automatiquement confié à un **pair tiré au sort** pour être noté et commenté ;
- le formateur dispose d'un **tableau de bord** par promotion : présences, exercices déposés, moyenne des notes reçues et relectures encore dues.

L'objectif est que le formateur sache, sans calcul manuel, qui était là, qui a travaillé et qui ne joue pas le jeu de la relecture.

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| **Formateur** | Ouvrir une session et obtenir son code ; ajouter une présence à la main ; clôturer une session ; consulter le tableau de sa promotion et le détail d'une session | Noter un exercice ; modifier une note rendue |
| **Étudiant** | Choisir son nom dans la liste de sa promotion ; marquer sa présence avec un code ; déposer puis remplacer le lien de son exercice ; voir la note et le commentaire reçus | Voir le nom de son relecteur ; déposer deux exercices pour la même session ; marquer sa présence avec un code expiré |
| **Relecteur** | Voir les exercices qui lui sont assignés ; enregistrer un brouillon de note ; rendre sa relecture de façon définitive | Relire son propre exercice ; choisir l'exercice qu'il relit ; modifier une relecture rendue |
| **Système** | Générer le code ; tirer au sort le relecteur ; bloquer un étudiant après cinq codes faux | — |

**Le relecteur n'est pas un acteur distinct : c'est un étudiant dans un certain rôle.** Dans le modèle de données, il n'existe donc pas de table `relecteur` : une `relecture` référence un `etudiant` par la colonne `relecteur_id`. Un même étudiant est à la fois auteur de son exercice et relecteur de celui d'un pair.

## 3. Périmètre

**Inclus dans cette version :**
- ouverture de session avec code de présence expirant, marquage de présence par code, blocage anti-devinette ;
- présence ajoutée à la main par le formateur, visiblement distinguée ;
- dépôt et remplacement du lien d'exercice ;
- assignation automatique et aléatoire d'un relecteur unique par exercice ;
- relecture avec brouillon puis validation définitive ;
- clôture de session par le formateur ;
- tableau récapitulatif par promotion, calculé par le serveur ;
- données de démonstration (une promotion, des étudiants, des sessions) chargées au démarrage.

**Explicitement exclu :**
- **authentification et mots de passe** : l'utilisateur choisit son nom dans une liste (Q1) ;
- gestion (création, modification) des promotions, des étudiants et des formateurs : ils sont fournis par les données de démonstration ;
- plusieurs formateurs et droits différenciés entre formateurs ;
- notifications (courriel, SMS) ;
- export du tableau (CSV, PDF) ;
- stockage des fichiers d'exercice : seul un lien est enregistré ;
- réouverture d'une session clôturée ;
- **sorti du périmètre à l'étape 3 (v2)** pour absorber le changement « deux relecteurs » : le blocage après 5 codes faux (EF13, RG4, issue #15) et le détail d'une session (EF15, issue #17). Justification en section 7.

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | Le formateur ouvre une session et obtient un code de présence | Quand j'ouvre une session avec un titre et une promotion, alors j'obtiens un code de 6 caractères et une heure d'expiration à +15 min ; sans titre, j'obtiens `400 CHAMP_MANQUANT` | Must |
| EF2 | L'étudiant choisit son nom dans la liste de sa promotion | Quand j'ouvre l'écran étudiant et que je choisis ma promotion, alors je vois la liste de ses étudiants et je peux sélectionner mon nom | Must |
| EF3 | L'étudiant marque sa présence à l'aide d'un code | Quand je saisis un code valide et non expiré, alors ma présence apparaît dans le tableau du formateur ; code expiré → `410`, déjà présent → `409`, code inconnu → `400` | Must |
| EF4 | L'étudiant dépose le lien de son exercice pour une session | Quand je dépose un lien `http(s)` valide, alors l'exercice est créé (`201`) ; un second dépôt pour la même session → `409 EXERCICE_DEJA_DEPOSE` ; un lien invalide → `400 LIEN_INVALIDE` | Must |
| EF5 | *(v2)* Le système assigne automatiquement **deux** relecteurs distincts à chaque exercice | Quand un exercice est déposé et qu'au moins deux autres étudiants sont présents, alors deux relectures sont créées pour deux relecteurs **différents** et l'exercice passe `EN_ATTENTE_RELECTURE` ; s'il n'y a qu'un pair éligible, il est assigné et le second l'est dès qu'un autre présent arrive ; l'auteur n'est jamais relecteur | Must |
| EF6 | Le relecteur voit les relectures qui lui sont assignées | Quand je choisis mon nom sur l'écran relecteur, alors je vois la liste des exercices à relire avec leur lien et leur statut | Must |
| EF7 | Le relecteur rend sa relecture (note et commentaire) de façon définitive | Quand j'envoie une note entière de 0 à 20 et un commentaire, alors j'obtiens `200` et la note compte dans la note retenue de l'exercice ; l'exercice passe `PARTIELLEMENT_RELU` (première relecture rendue) puis `RELU` (seconde) ; un second envoi → `409`, une note de 21 ou de 12,5 → `400` | Must |
| EF8 | Le formateur consulte le tableau de sa promotion | Quand j'ouvre le tableau d'une promotion, alors je vois pour chaque étudiant : nombre de présences, nombre d'exercices déposés, moyenne de ses notes retenues (vide s'il n'en a aucune) *(v2)* **marquée provisoire** si l'une d'elles ne repose que sur une relecture, et nombre de relectures qu'il doit encore faire ; promotion inconnue → `404` | Must |
| EF9 | Le relecteur enregistre un brouillon modifiable | Quand j'enregistre un brouillon, alors je peux le modifier autant de fois que je veux tant que je ne l'ai pas rendu et que la session n'est pas clôturée | Should |
| EF10 | L'étudiant remplace le lien de son exercice | Quand je remplace mon lien alors que mon relecteur n'a encore rien enregistré, alors le nouveau lien est pris en compte ; s'il a commencé → `409 RELECTURE_COMMENCEE` | Should |
| EF11 | Le formateur ajoute une présence à la main | Quand j'ajoute un étudiant présent à la main, alors la présence est créée avec `source = FORMATEUR` et elle est affichée « ajoutée par le formateur » | Should |
| EF12 | Le formateur clôture une session | Quand je clôture une session, alors plus aucun dépôt, remplacement de lien, brouillon ni relecture n'est accepté pour elle (`409 SESSION_CLOTUREE`) et les exercices non relus restent visibles « en attente » | Should |
| EF13 | Le système bloque un étudiant qui se trompe de code cinq fois | Quand je saisis 5 codes inconnus d'affilée, alors toute nouvelle tentative pendant 2 minutes renvoie `429 TROP_DE_TENTATIVES`, même avec le bon code | ~~Should~~ **Won't (v2)** |
| EF14 | L'étudiant voit la note retenue et les commentaires reçus | Quand au moins une relecture a été rendue, alors je vois la note retenue *(v2)* marquée **provisoire** s'il n'y en a qu'une, et les commentaires, mais jamais le nom de mes relecteurs (la réponse de l'API ne le contient pas) | ~~Should~~ **Must (v2)** |
| EF15 | Le formateur voit le détail d'une session | Quand j'ouvre une session, alors je vois la liste des présents avec leur source, et la liste des exercices avec leur statut, les exercices « en attente » étant clairement identifiés | ~~Could~~ **Won't (v2)** |

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | L'écran de marquage de présence est utilisable sur un téléphone (largeur 360 px) | Ouverture de l'écran étudiant dans l'outil « mode mobile » du navigateur : aucun défilement horizontal, champ de code et bouton accessibles |
| ENF2 | Le tableau du formateur répond en moins de 2 s pour une promotion de 60 étudiants et 30 sessions | Le tableau est calculé en requêtes agrégées (pas de requête par étudiant) ; mesure dans l'onglet Réseau du navigateur |
| ENF3 | Volumétrie cible : 1 formateur, 1 à 5 promotions de 60 étudiants au plus, 60 marquages de présence dans la même minute | Test manuel : 60 présences créées à la suite sans erreur |
| ENF4 | Toute erreur renvoie le format `{ code, message }`, jamais de stack trace ni de corps vide | Tests d'intégration sur les cas d'erreur ; aucun champ `trace` dans les réponses |
| ENF5 | L'application démarre chez un tiers en trois commandes maximum, avec des données de démonstration | README exécuté depuis un clone vierge dans un dossier vide |
| ENF6 | Aucune dépendance externe à installer autre que Java 17+ et Node 18+ | Base H2 embarquée ; pas de Docker ni de serveur de base de données requis |
| ENF7 | Les heures sont stockées et échangées en UTC, au format ISO-8601 | Les champs `ouvertureAt` et `expirationAt` se terminent par `Z` |

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Un code de présence expire **15 minutes** après l'ouverture de la session | Q2 |
| RG2 | On ne peut plus marquer sa présence avec le code après son expiration **ni après la clôture** de la session : réponse `410 CODE_EXPIRE` | Q3 |
| RG3 | Un étudiant a **au plus une présence** par session : second marquage → `409 DEJA_PRESENT` | Hypothèse (contrat) |
| RG4 | Après **5 codes inconnus consécutifs**, l'étudiant est bloqué **2 minutes** (`429 TROP_DE_TENTATIVES`). Le compteur est remis à zéro par un marquage réussi ou à la fin du blocage. Un code expiré n'est pas compté comme une erreur de saisie | Q4 |
| RG5 | Un étudiant ne peut **jamais** relire son propre exercice (`403 AUTO_RELECTURE`) | Q5 |
| RG6 | *(v2)* Un exercice a **deux relecteurs différents** ; un même étudiant ne relit jamais deux fois le même exercice. ~~Un exercice a un seul relecteur~~ | Changement étape 3 (remplace Q6) |
| RG7 | Chaque relecteur est tiré **au hasard** par le système parmi les étudiants **présents à la session** de l'exercice, auteur et relecteur déjà assigné exclus. À chance égale, le tirage se fait parmi ceux qui ont le moins de relectures dans cette session, pour répartir la charge | Q7 |
| RG8 | L'étudiant relu voit sa note et le commentaire, **jamais le nom** de son relecteur | Q8 |
| RG9 | Une note est un **entier de 0 à 20** ; une valeur décimale ou hors bornes → `400 NOTE_INVALIDE` | Q9 |
| RG10 | Une relecture passe par un **brouillon modifiable** tant qu'elle n'est pas rendue et que la session n'est pas clôturée. Une fois **rendue** (validée), elle est **définitive** : `409 RELECTURE_DEJA_RENDUE` | Q10, Q15 — voir section 7 |
| RG11 | Un exercice dont la relecture n'est pas rendue reste « en attente », y compris après la clôture, et apparaît comme tel au formateur | Q11 |
| RG12 | Un exercice peut être déposé **jusqu'à la clôture** de la session, même après l'expiration du code | Q12 |
| RG13 | Un étudiant dépose **au plus un exercice** par session : second dépôt → `409 EXERCICE_DEJA_DEPOSE` | Hypothèse (contrat) |
| RG14 | Le lien d'un exercice peut être remplacé tant que **toutes** ses relectures sont au statut `A_FAIRE` (aucun brouillon ni rendu) et que la session n'est pas clôturée | Q13 |
| RG15 | Une présence ajoutée par le formateur porte `source = FORMATEUR` et est affichée « ajoutée par le formateur » ; une présence par code porte `source = ETUDIANT` | Q14 |
| RG16 | *(v2)* La **note retenue** d'un exercice est la moyenne des notes de ses relectures **rendues** ; elle est **provisoire** tant qu'une seule des deux est rendue. La **moyenne** d'un étudiant est la moyenne de ses notes retenues, arrondie à 2 décimales, `null` s'il n'en a aucune, et marquée provisoire si l'une d'elles l'est. Tout est calculé **uniquement par le serveur** | Q16, F3, changement étape 3 |
| RG17 | *(v2)* Un exercice qui n'a pas encore ses deux relecteurs (aucun pair éligible, ou un seul) les reçoit automatiquement dès qu'un étudiant éligible marque sa présence (ou est ajouté par le formateur) à cette session. Il reste `DEPOSE` tant qu'il n'en a aucun | Hypothèse — voir section 7 |
| RG18 | Un lien d'exercice est une URL absolue `http://` ou `https://` de 500 caractères au plus | Hypothèse (contrat `400 LIEN_INVALIDE`) |
| RG19 | Un étudiant ne peut marquer sa présence ou déposer un exercice que pour une session de **sa promotion** | Hypothèse |
| RG20 | Le code de présence fait 6 caractères majuscules et chiffres, sans caractères ambigus (`0/O`, `1/I`) ; il est unique ; sa saisie est insensible à la casse et aux espaces | Hypothèse (ENF1) |

## 7. Zones d'ombre, hypothèses et contradictions

**Points que la demande ne tranche pas :**

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Pourquoi |
|---|---|---|---|
| **Le trou : quand le relecteur est-il tiré au sort, et que se passe-t-il si personne n'est éligible ?** | Q7 dit « parmi les présents », Q12 autorise le dépôt après la séance. Personne n'a demandé ce qui se passe si l'auteur est le seul présent, ou s'il dépose avant l'arrivée des autres | **Ma décision :** le tirage a lieu **au moment du dépôt**. S'il n'y a aucun candidat, l'exercice reste `DEPOSE` et il est assigné dès qu'un présent éligible marque sa présence (RG17). Conséquence : statut `DEPOSE` dans le cycle de vie (D4) | C'est mon idée : tirer au dépôt donne tout de suite un relecteur dans le cas normal, et la file d'attente évite de refuser un dépôt légitime (Q12) ou de violer Q5/Q7 en tirant un absent ou l'auteur |
| Qu'est-ce que « la fin de la session » (Q3) ? | La demande ne définit pas d'heure de fin, seulement une expiration du code (Q2) et une clôture (Q10, Q12) | La présence se termine à l'expiration du code **ou** à la clôture ; les dépôts et relectures se terminent à la clôture (RG2, RG12, champ `cloturee_at`) | Q2 dit « après, il ne marche plus » : le code est la seule preuve de présence dans la salle ; Q10 et Q12 citent explicitement la clôture comme seule borne pour le reste |
| Qu'est-ce que « commencer à relire » (Q13) ? | Rien ne le définit, le système ne sait pas si le relecteur a ouvert le lien | La relecture est commencée dès qu'un **brouillon** est enregistré (RG14, statut `BROUILLON`) | C'est le seul événement que le système observe ; ouvrir un lien externe n'est pas traçable |
| Faut-il être présent pour déposer un exercice ? | Non abordé | Non : un étudiant absent peut déposer ; en revanche il ne peut pas être tiré comme relecteur | Q12 vise justement ceux qui déposent plus tard ; Q7 ne parle que du choix du relecteur |
| Le blocage de Q4 est-il par session ou global ? | « Bloquez-le » | Par étudiant, toutes sessions confondues ; seuls les **codes inconnus** comptent (RG4, table `tentative_code`, réponse `429`) | Q4 vise la devinette de codes : un code expiré prouve que l'étudiant connaît le vrai code, ce n'est pas une devinette. `429 Too Many Requests` est le code HTTP standard de ce cas |
| Qui envoie la relecture, sans authentification (Q1) ? | Le contrat prévoit `403` pour l'auto-relecture mais pas d'identité | L'écran envoie l'étudiant sélectionné dans l'en-tête facultatif `X-Etudiant-Id`. S'il est l'auteur → `403 AUTO_RELECTURE` ; s'il n'est pas le relecteur assigné → `403 RELECTEUR_NON_ASSIGNE` | Le corps imposé `{ note, commentaire }` ne peut pas être modifié ; un en-tête facultatif ajoute l'identité sans casser le contrat |
| Que voit le tableau de « sa présence à chaque session » (Q16) ? | Le contrat imposé ne renvoie qu'un nombre `presences` | Le tableau affiche le **nombre** ; le détail par session est dans l'écran « détail de session » (EF15, `GET /api/sessions/{id}/presences`) | Je ne peux pas changer la réponse imposée du tableau ; le détail passe donc par une opération ajoutée |
| Une référence inconnue dans un corps de requête (étudiant, session, promotion) | Le contrat imposé ne prévoit que `400` pour ces opérations | `400` avec un code explicite (`ETUDIANT_INCONNU`, `SESSION_INCONNUE`, `PROMOTION_INCONNUE`) ; `404` seulement pour un identifiant de **chemin** (`/api/relectures/{id}` → `404 RELECTURE_INCONNUE`) | Un identifiant inconnu dans le corps rend la requête invalide (400) ; une ressource de l'URL introuvable est un 404. Je reste ainsi dans les codes prévus par le contrat |

**Contradictions relevées :**

| Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|
| **Q10** (« le relecteur peut corriger sa note tant que la session n'est pas clôturée ») contre **Q15** (« une fois que le relecteur a validé, c'est fini ») | **Ma décision :** la relecture a deux temps. Un **brouillon**, modifiable tant que la session est ouverte, respecte Q10. La **validation** (`POST /api/relectures/{id}`) est définitive et respecte Q15 | C'est mon idée de concilier les deux réponses plutôt que d'en sacrifier une. Q10 exprime un besoin réel (se corriger avant de s'engager) et Q15 un principe d'honnêteté (ne plus revenir sur une note rendue). Le contrat imposé tranche d'ailleurs dans ce sens : il prévoit `409 RELECTURE_DEJA_RENDUE` sur un second envoi |
| Q3 (pas de présence après la fin de session) et Q12 (dépôt possible après la fin) | Ce n'est pas une contradiction : ce sont deux fenêtres différentes, la présence et le dépôt | RG2 et RG12 bornent chacune la sienne |
| Q11 (« l'exercice reste en attente ») et Q10 (corrections possibles jusqu'à la clôture) | Après la clôture, une relecture non rendue ne peut plus l'être : l'exercice reste « en attente » pour toujours, et c'est visible | Q11 demande justement que ce cas soit **visible**, pas résolu automatiquement |

**Changement de besoin de l'étape 3 (v2) : « chaque exercice est relu par deux pairs différents » :**

Ce changement **remplace Q6** (« un seul relecteur ») et rend RG6, RG7, RG14, RG16, RG17, EF5, EF7, EF8, EF14 et les diagrammes D2 et D4 obsolètes : ils sont réécrits ci-dessus et dans `docs/diagrammes/`.

| Point laissé ouvert par le client | Décision retenue | Pourquoi |
|---|---|---|
| La note provisoire compte-t-elle dans la moyenne du tableau ? | **Ma décision :** oui, et le tableau l'indique (`moyenneProvisoire`) | Le client veut justement éviter qu'un étudiant « n'ait aucune note » ; une moyenne cachée irait contre sa demande, une moyenne non signalée le tromperait |
| Que faire s'il n'y a qu'un seul pair éligible au dépôt ? | **Ma décision :** l'assigner tout de suite ; le second est tiré dès qu'un autre présent arrive (RG17) | Même logique que ma décision sur le trou de l'étape 1 : ne jamais bloquer, ne jamais tirer un absent ou l'auteur |
| Que deviennent les exercices déjà relus par un seul pair (base existante) ? | **Ma décision :** la migration V2 les passe `PARTIELLEMENT_RELU` (note provisoire). Pour une session ouverte, le second relecteur est tiré à la prochaine présence ; pour une session clôturée, la note reste provisoire | La migration ne tire pas au sort : un tirage doit suivre RG7 (présents, charge) et se fait dans le service, pas en SQL. Aucune donnée n'est perdue |
| **Sacrifice de périmètre** : le changement est un Must qui arrive tard | **Ma décision :** sortir **EF13 / RG4** (blocage après 5 codes faux, #15) et **EF15** (détail de session, #17) ; en échange, **EF14** (l'étudiant voit sa note) passe Must | RG4 protège contre la devinette de codes, mais le code expire en 15 min (RG1) : le risque est limité. EF15 était déjà un Could, et le tableau montre déjà les relectures en attente (Q11). EF14 devient nécessaire : le client veut que la note provisoire soit « affichée » |

**Choix techniques qui sont les miens :**

| Point | Ma décision | Pourquoi |
|---|---|---|
| Base de données | **H2 embarquée** (fichier), schéma géré par **Flyway** | Le correcteur n'a rien à installer ; le démarrage tient en trois commandes |

## 8. Contraintes techniques

**Imposées par le sujet :**

| # | Contrainte | Comment je la respecte |
|---|---|---|
| B1 | Java 17+, Maven, wrapper `mvnw` commité | Spring Boot 4, compilation en Java 17 (je développe avec le JDK 21) ; `mvnw` et `.mvn/wrapper/` versionnés |
| B2 | Contrat `api/contrat.yaml` respecté à la lettre | Contrat complété et figé avant le premier commit de code ; tests d'intégration sur les codes HTTP |
| B3 | Séparation contrôleur / service / repository, DTO | Paquets `web` (contrôleurs + DTO `record`), `service`, `repository`, `domain` ; aucune entité JPA renvoyée en JSON |
| B4 | Validation et `@RestControllerAdvice` | Bean Validation sur les DTO ; une exception métier par code d'erreur ; un gestionnaire unique qui produit `{ code, message }`, y compris pour le JSON mal formé et les erreurs imprévues (`500 ERREUR_INTERNE` sans stack trace) |
| B5 | Schéma versionné, pas de `ddl-auto=update` | Flyway, migrations `V1__...sql` ; `spring.jpa.hibernate.ddl-auto=validate` |
| B6 | Un test unitaire métier et un test d'intégration | JUnit 5 : test unitaire des règles RG1/RG5/RG9 ; test `@SpringBootTest` + MockMvc sur `POST /api/presences` avec H2 en mémoire |
| F1 | Framework déclaré et justifié, le build passe | React, justifié dans le README ; `npm run build` vérifié |
| F2 | Trois écrans | Formateur, Étudiant, Relecteur |
| F3 | Couche API dédiée, chargement/erreur, pas de règle dupliquée | Un module `src/api/` unique ; états `chargement` / `erreur` sur chaque appel ; la moyenne est affichée telle que l'API la renvoie |

**Que je m'impose :**
- Git : une branche par issue (`feat/5-marquer-presence`), une PR par branche, `Closes #n` dans le commit final et dans la PR ; `main` ne reçoit que des merges de PR ; messages de commit au format `type(portée): résumé (RGx)` ;
- base de test en mémoire, indépendante de toute base locale ;
- aucune donnée sensible : aucun secret, aucun mot de passe (Q1).

## 9. Livrables

- Dépôt public `kfokam48-epreuve-KF48-YAO-165` :
  - `docs/CAHIER_DES_CHARGES.md` (ce document), `docs/JOURNAL.md` ;
  - `docs/diagrammes/` : D1 cas d'utilisation, D2 modèle de données, D3 séquence « marquer sa présence », D4 cycle de vie d'un exercice (Mermaid) ;
  - `api/contrat.yaml` complété ;
  - `backend/` Spring Boot avec migrations Flyway, données de démonstration et tests ;
  - `frontend/` React ;
  - `README.md` d'installation, `CHANGELOG.md` ;
  - backlog en issues GitHub, PR liées ;
  - trois commits jalons `[JALON] analyse`, `[JALON] v0.1`, `[JALON] v1.0`.
- Dépôt public séparé `kfokam48-gitlab-KF48-YAO-165` (épreuve Git).
- `SOUMISSION.md` déposé sur la plateforme.

## 10. Démarche prévue

1. **Analyse** (ce document, diagrammes, contrat, issues) → `[JALON] analyse`. Aucun code avant ce jalon.
2. **v0.1** : uniquement les issues **Must** (EF1 à EF8), dans l'ordre des dépendances : socle backend et schéma → sessions → présences → exercices et assignation → relectures → tableau → écrans React. Une branche et une PR par issue → `[JALON] v0.1`.
3. **Enveloppe** : j'ouvre une issue pour le bug et une pour l'évolution **avant** de coder ; je reproduis le bug par un test ; je versionne la migration ; je mets à jour le contrat, puis ce document et les diagrammes dans un commit dédié ; je re-priorise le backlog par écrit.
4. **v1.0** : les issues **Should** dans la limite du temps, `CHANGELOG.md`, README testé depuis un clone vierge → `[JALON] v1.0`.
5. **Épreuve Git** sur le bundle, dans un dépôt séparé.
6. **Soumission** au plus tard vers 17h00, pas à 17h58.

**Si je prends du retard :** je sacrifie d'abord les **Could**, puis les **Should** dans l'ordre inverse de leur numéro. Je garde toujours la marge nécessaire aux étapes 5 et 6, qui valent plus de points que les dernières fonctionnalités.

**Definition of Done — un ticket est terminé quand :**
- ses critères d'acceptation sont vérifiés (par un test automatique quand il touche une règle `RGx`, sinon à la main) ;
- les codes HTTP et le format d'erreur correspondent au contrat ;
- le build backend (`./mvnw verify`) et, s'il est concerné, le build frontend (`npm run build`) passent ;
- le dernier commit de la branche et la PR portent `Closes #n` ; la PR est fusionnée dans `main`, ce qui ferme l'issue ;
- la documentation touchée (contrat, diagrammes, ce document) est à jour.

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 25/09/2026, étape 1 | Version initiale |
| 2 | 25/09/2026, étape 3 | Changement de besoin « deux relecteurs par exercice, note retenue = moyenne, provisoire si une seule » : RG6 (remplace Q6), RG7, RG14, RG16, RG17, EF5, EF7, EF8, EF14 réécrites ; section 7 complétée ; EF13 et EF15 sortis du périmètre, EF14 passé Must |
