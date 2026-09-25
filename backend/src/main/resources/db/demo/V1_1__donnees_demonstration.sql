-- Données de démonstration (ENF5). Chargées au démarrage, jamais dans les tests.
-- Elles illustrent chaque état utile au correcteur :
--   * session 1 clôturée : relectures rendues, une relecture jamais faite (RG11),
--     un brouillon figé par la clôture, une présence ajoutée par le formateur (RG15) ;
--   * session 2 ouverte (code expiré) : dépôts et relectures en cours ;
--   * session 3 : un exercice DEPOSE sans relecteur éligible (RG17).
-- Pour tester le marquage de présence, ouvrir une nouvelle session depuis l'écran Formateur.

INSERT INTO promotion (id, nom) VALUES
    (1, 'KFOKAM48 - Promotion 2026'),
    (2, 'KFOKAM48 - Cours du soir');

INSERT INTO etudiant (id, nom, promotion_id) VALUES
    (1, 'Atangana Carine', 1),
    (2, 'Bello Ibrahim', 1),
    (3, 'Djomo Kevin', 1),
    (4, 'Essomba Laure', 1),
    (5, 'Fotso Arnaud', 1),
    (6, 'Kamga Sandrine', 1),
    (7, 'Mbarga Yannick', 1),
    (8, 'Nkoulou Brice', 1),
    (9, 'Ondoa Mireille', 2),
    (10, 'Tchinda Herve', 2),
    (11, 'Wamba Lydie', 2);

INSERT INTO session_cours (id, titre, promotion_id, code, ouverture_at, expiration_at, cloturee_at) VALUES
    (1, 'Spring Boot - les bases', 1, 'DMA2B3',
        DATEADD('DAY', -7, LOCALTIMESTAMP), DATEADD('MINUTE', 15, DATEADD('DAY', -7, LOCALTIMESTAMP)),
        DATEADD('DAY', -6, LOCALTIMESTAMP)),
    (2, 'React - composants et etat', 1, 'DMC4D5',
        DATEADD('DAY', -2, LOCALTIMESTAMP), DATEADD('MINUTE', 15, DATEADD('DAY', -2, LOCALTIMESTAMP)),
        NULL),
    (3, 'Git - branches et pull requests', 2, 'DME6F7',
        DATEADD('DAY', -3, LOCALTIMESTAMP), DATEADD('MINUTE', 15, DATEADD('DAY', -3, LOCALTIMESTAMP)),
        NULL);

INSERT INTO presence (session_id, etudiant_id, source, marquee_at) VALUES
    (1, 1, 'ETUDIANT',  DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (1, 2, 'ETUDIANT',  DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (1, 3, 'ETUDIANT',  DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (1, 4, 'ETUDIANT',  DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (1, 5, 'ETUDIANT',  DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (1, 6, 'FORMATEUR', DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (1, 7, 'ETUDIANT',  DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (2, 1, 'ETUDIANT',  DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (2, 2, 'ETUDIANT',  DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (2, 3, 'ETUDIANT',  DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (2, 4, 'ETUDIANT',  DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (2, 5, 'ETUDIANT',  DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (2, 8, 'ETUDIANT',  DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (3, 9, 'ETUDIANT',  DATEADD('DAY', -3, LOCALTIMESTAMP));

INSERT INTO exercice (id, session_id, etudiant_id, lien, statut, depose_at) VALUES
    (1, 1, 1, 'https://github.com/demo-kfokam48/atangana-spring-bases', 'RELU', DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (2, 1, 2, 'https://github.com/demo-kfokam48/bello-spring-bases', 'RELU', DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (3, 1, 3, 'https://github.com/demo-kfokam48/djomo-spring-bases', 'RELU', DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (4, 1, 4, 'https://github.com/demo-kfokam48/essomba-spring-bases', 'EN_ATTENTE_RELECTURE', DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (5, 1, 5, 'https://github.com/demo-kfokam48/fotso-spring-bases', 'RELU', DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (6, 1, 6, 'https://github.com/demo-kfokam48/kamga-spring-bases', 'EN_ATTENTE_RELECTURE', DATEADD('DAY', -7, LOCALTIMESTAMP)),
    (7, 2, 1, 'https://github.com/demo-kfokam48/atangana-react', 'EN_ATTENTE_RELECTURE', DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (8, 2, 2, 'https://github.com/demo-kfokam48/bello-react', 'EN_ATTENTE_RELECTURE', DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (9, 2, 8, 'https://github.com/demo-kfokam48/nkoulou-react', 'RELU', DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (10, 2, 3, 'https://github.com/demo-kfokam48/djomo-react', 'EN_ATTENTE_RELECTURE', DATEADD('DAY', -2, LOCALTIMESTAMP)),
    (11, 3, 9, 'https://github.com/demo-kfokam48/ondoa-git', 'DEPOSE', DATEADD('DAY', -3, LOCALTIMESTAMP));

INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, statut, assignee_at, modifiee_at, rendue_at) VALUES
    (1, 2, 15, 'Code clair, tests presents. Manque la validation des entrees.', 'RENDUE',
        DATEADD('DAY', -7, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP)),
    (2, 3, 12, 'Fonctionne, mais le controleur accede directement au repository.', 'RENDUE',
        DATEADD('DAY', -7, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP)),
    (3, 1, 17, 'Tres bon decoupage en couches.', 'RENDUE',
        DATEADD('DAY', -7, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP)),
    (4, 5, NULL, NULL, 'A_FAIRE',
        DATEADD('DAY', -7, LOCALTIMESTAMP), NULL, NULL),
    (5, 4, 9, 'Ne compile pas sans modification du pom.xml.', 'RENDUE',
        DATEADD('DAY', -7, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP), DATEADD('DAY', -6, LOCALTIMESTAMP)),
    (6, 7, 14, 'Brouillon : a completer', 'BROUILLON',
        DATEADD('DAY', -7, LOCALTIMESTAMP), DATEADD('DAY', -7, LOCALTIMESTAMP), NULL),
    (7, 3, NULL, NULL, 'A_FAIRE',
        DATEADD('DAY', -2, LOCALTIMESTAMP), NULL, NULL),
    (8, 4, 11, 'Composants trop gros, a decouper', 'BROUILLON',
        DATEADD('DAY', -2, LOCALTIMESTAMP), DATEADD('DAY', -1, LOCALTIMESTAMP), NULL),
    (9, 5, 16, 'Bonne gestion des etats de chargement.', 'RENDUE',
        DATEADD('DAY', -2, LOCALTIMESTAMP), DATEADD('DAY', -1, LOCALTIMESTAMP), DATEADD('DAY', -1, LOCALTIMESTAMP)),
    (10, 1, NULL, NULL, 'A_FAIRE',
        DATEADD('DAY', -2, LOCALTIMESTAMP), NULL, NULL);

-- Les identifiants explicites ci-dessus n'avancent pas les compteurs IDENTITY : on les recale.
ALTER TABLE promotion ALTER COLUMN id RESTART WITH 100;
ALTER TABLE etudiant ALTER COLUMN id RESTART WITH 100;
ALTER TABLE session_cours ALTER COLUMN id RESTART WITH 100;
ALTER TABLE exercice ALTER COLUMN id RESTART WITH 100;
