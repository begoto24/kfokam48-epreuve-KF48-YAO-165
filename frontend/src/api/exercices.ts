import { appeler } from './client'
import type { ExerciceDepose } from './types'

export const deposerExercice = (sessionId: number, etudiantId: number, lien: string) =>
  appeler<ExerciceDepose>('/exercices', { methode: 'POST', corps: { sessionId, etudiantId, lien } })
