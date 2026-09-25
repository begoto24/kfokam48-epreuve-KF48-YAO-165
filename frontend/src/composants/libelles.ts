import type { StatutExercice, StatutRelecture } from '../api/types'

/** Libellés d'affichage des statuts renvoyés par l'API (aucune règle métier ici). */
export const LIBELLE_STATUT_EXERCICE: Record<StatutExercice, string> = {
  DEPOSE: 'Déposé — en attente de relecteurs',
  EN_ATTENTE_RELECTURE: 'En attente de relecture',
  PARTIELLEMENT_RELU: 'Relu par un pair sur deux — note provisoire',
  RELU: 'Relu par les deux pairs',
}

export const LIBELLE_STATUT_RELECTURE: Record<StatutRelecture, string> = {
  A_FAIRE: 'À faire',
  BROUILLON: 'Brouillon',
  RENDUE: 'Rendue',
}
