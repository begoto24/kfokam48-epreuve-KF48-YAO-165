import { appeler } from './client'
import type { Relecture } from './types'

/** En-tête qui identifie l'étudiant sélectionné (pas d'authentification, Q1). */
const entete = (etudiantId: number) => ({ 'X-Etudiant-Id': String(etudiantId) })

export const listerRelectures = (etudiantId: number) => appeler<Relecture[]>(`/etudiants/${etudiantId}/relectures`)

export const rendreRelecture = (relectureId: number, etudiantId: number, note: number, commentaire: string) =>
  appeler<Relecture>(`/relectures/${relectureId}`, {
    methode: 'POST',
    corps: { note, commentaire },
    entetes: entete(etudiantId),
  })
