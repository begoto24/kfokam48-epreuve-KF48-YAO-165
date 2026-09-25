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

**Fait :**

**Bloqué :**

**IA :**

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**

---

## Étape 4 — Version finale

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 5 — Épreuve Git

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 6 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
