-- V2 — Changement de besoin de l'étape 3 : chaque exercice est relu par DEUX pairs différents (issue #34).
-- Reflète docs/diagrammes/D2-modele-donnees.md (version 2). V1 n'est pas modifiée.
-- Doit fonctionner sur une base déjà remplie : aucune ligne n'est supprimée.

-- RG6 (v2) : plus « une relecture par exercice », mais « un relecteur au plus une fois par exercice »
ALTER TABLE relecture DROP CONSTRAINT uk_relecture_exercice;
ALTER TABLE relecture ADD CONSTRAINT uk_relecture_exercice_relecteur UNIQUE (exercice_id, relecteur_id);

-- Nouveau statut : une relecture rendue sur deux, note retenue provisoire (RG16)
ALTER TABLE exercice DROP CONSTRAINT ck_exercice_statut;
ALTER TABLE exercice ADD CONSTRAINT ck_exercice_statut
    CHECK (statut IN ('DEPOSE', 'EN_ATTENTE_RELECTURE', 'PARTIELLEMENT_RELU', 'RELU'));

-- Données existantes : un exercice RELU ne l'a été que par un seul pair ; sa note devient provisoire.
-- Le second relecteur n'est PAS tiré ici (RG7 s'applique dans le service, à la prochaine présence).
UPDATE exercice SET statut = 'PARTIELLEMENT_RELU' WHERE statut = 'RELU';
