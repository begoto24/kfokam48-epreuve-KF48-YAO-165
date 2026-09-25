import { appeler } from './client'
import type { ExerciceDepose, MonExercice } from './types'

export const deposerExercice = (sessionId: number, etudiantId: number, lien: string) =>
  appeler<ExerciceDepose>('/exercices', { methode: 'POST', corps: { sessionId, etudiantId, lien } })

export const listerMesExercices = (etudiantId: number) => appeler<MonExercice[]>(`/etudiants/${etudiantId}/exercices`)
