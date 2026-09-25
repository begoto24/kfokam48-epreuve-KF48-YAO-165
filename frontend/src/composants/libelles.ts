import type { StatutExercice, StatutRelecture } from '../api/types'

/** Libellés d'affichage des statuts renvoyés par l'API (aucune règle métier ici). */
export const LIBELLE_STATUT_EXERCICE: Record<StatutExercice, string> = {
  DEPOSE: 'Déposé — en attente d’un relecteur',
  EN_ATTENTE_RELECTURE: 'En attente de relecture',
  RELU: 'Relu',
}

export const LIBELLE_STATUT_RELECTURE: Record<StatutRelecture, string> = {
  A_FAIRE: 'À faire',
  BROUILLON: 'Brouillon',
  RENDUE: 'Rendue',
}
