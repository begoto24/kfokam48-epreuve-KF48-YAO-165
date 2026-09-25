# D1 — Cas d'utilisation

Mermaid n'a pas de diagramme de cas d'utilisation natif : les acteurs sont représentés à gauche et à droite, les cas d'utilisation par des ovales à l'intérieur du cadre du système. Les liens `include` et `extend` suivent la notation UML.

Le **relecteur** est un étudiant dans un rôle particulier (cahier des charges, section 2) : il hérite de l'acteur Étudiant.

```mermaid
flowchart LR
    F(["👤 Formateur"])
    E(["👤 Étudiant"])
    R(["👤 Relecteur"])
    S(["⚙️ Système"])

    R -.->|"hérite de"| E

    subgraph SYS["PrésencePair"]
        UC1(["UC1 Ouvrir une session<br/>et obtenir un code — EF1"])
        UC2(["UC2 Ajouter une présence<br/>à la main — EF11"])
        UC3(["UC3 Clôturer une session — EF12"])
        UC4(["UC4 Consulter le tableau<br/>de la promotion — EF8"])
        UC5(["UC5 Voir le détail<br/>d'une session — EF15"])

        UC6(["UC6 Choisir son nom<br/>dans la liste — EF2"])
        UC7(["UC7 Marquer sa présence<br/>avec un code — EF3"])
        UC8(["UC8 Déposer le lien<br/>de son exercice — EF4"])
        UC9(["UC9 Remplacer le lien<br/>de son exercice — EF10"])
        UC10(["UC10 Voir la note et le<br/>commentaire reçus — EF14"])

        UC11(["UC11 Voir les relectures<br/>assignées — EF6"])
        UC12(["UC12 Enregistrer un brouillon<br/>de relecture — EF9"])
        UC13(["UC13 Rendre sa relecture<br/>définitivement — EF7"])

        UC14(["UC14 Assigner un relecteur<br/>au hasard — EF5"])
        UC15(["UC15 Bloquer après<br/>5 codes faux — EF13"])
    end

    F --- UC1
    F --- UC2
    F --- UC3
    F --- UC4
    F --- UC5

    E --- UC6
    E --- UC7
    E --- UC8
    E --- UC9
    E --- UC10

    R --- UC11
    R --- UC12
    R --- UC13

    UC7 -.->|"«include»"| UC6
    UC8 -.->|"«include»"| UC6
    UC11 -.->|"«include»"| UC6
    UC8 -.->|"«include»"| UC14
    UC15 -.->|"«extend»<br/>code inconnu"| UC7
    UC7 -.->|"«extend»<br/>exercice en attente<br/>d'assignation (RG17)"| UC14
    UC2 -.->|"«extend»<br/>exercice en attente<br/>d'assignation (RG17)"| UC14
    UC13 -.->|"«extend»<br/>depuis un brouillon"| UC12

    UC14 --- S
    UC15 --- S
```

## Lecture

| Acteur | Cas d'utilisation | Priorité |
|---|---|---|
| Formateur | UC1, UC4 | Must |
| Formateur | UC2, UC3 | Should |
| Formateur | UC5 | Could |
| Étudiant | UC6, UC7, UC8 | Must |
| Étudiant | UC9, UC10 | Should |
| Relecteur | UC11, UC13 | Must |
| Relecteur | UC12 | Should |
| Système | UC14 (Must), UC15 (Should) | — |
