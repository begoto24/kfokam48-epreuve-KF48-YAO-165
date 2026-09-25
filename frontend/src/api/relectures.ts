import { appeler } from './client'
import type { Relecture } from './types'

export const listerRelectures = (etudiantId: number) => appeler<Relecture[]>(`/etudiants/${etudiantId}/relectures`)
