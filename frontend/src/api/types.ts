// Types des réponses, recopiés de api/contrat.yaml (components/schemas).

export type Promotion = {
  id: number
  nom: string
}

export type Etudiant = {
  id: number
  nom: string
  promotionId: number
}

export type SessionOuverte = {
  id: number
  code: string
  ouvertureAt: string
  expirationAt: string
}

export type Presence = {
  id: number
  sessionId: number
  etudiantId: number
  source: 'ETUDIANT' | 'FORMATEUR'
}

export type Session = {
  id: number
  titre: string
  promotionId: number
  ouvertureAt: string
  expirationAt: string
  cloturee: boolean
  clotureeAt: string | null
}

export type StatutExercice = 'DEPOSE' | 'EN_ATTENTE_RELECTURE' | 'PARTIELLEMENT_RELU' | 'RELU'

export type ExerciceDepose = {
  id: number
  statut: StatutExercice
}

export type StatutRelecture = 'A_FAIRE' | 'BROUILLON' | 'RENDUE'

export type Relecture = {
  id: number
  exerciceId: number
  sessionTitre: string
  lien: string
  statut: StatutRelecture
  note: number | null
  commentaire: string | null
  sessionCloturee: boolean
}

export type LigneTableau = {
  etudiantId: number
  nom: string
  presences: number
  exercicesDeposes: number
  moyenne: number | null
  /** v1.2 : la moyenne contient au moins une note provisoire (une relecture rendue sur deux). */
  moyenneProvisoire: boolean
  relecturesEnAttente: number
}

/** Un exercice vu par son auteur : jamais d'information sur les relecteurs (RG8). */
export type MonExercice = {
  id: number
  sessionId: number
  sessionTitre: string
  lien: string
  statut: StatutExercice
  note: number | null
  noteProvisoire: boolean
  commentaires: string[]
}
