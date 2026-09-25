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

export type StatutExercice = 'DEPOSE' | 'EN_ATTENTE_RELECTURE' | 'RELU'

export type ExerciceDepose = {
  id: number
  statut: StatutExercice
}
