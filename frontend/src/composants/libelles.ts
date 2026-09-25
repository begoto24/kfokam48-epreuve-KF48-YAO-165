import type { StatutExercice } from '../api/types'

/** Libellés d'affichage des statuts renvoyés par l'API (aucune règle métier ici). */
export const LIBELLE_STATUT_EXERCICE: Record<StatutExercice, string> = {
  DEPOSE: 'Déposé — en attente d’un relecteur',
  EN_ATTENTE_RELECTURE: 'En attente de relecture',
  RELU: 'Relu',
}
