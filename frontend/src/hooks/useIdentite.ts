import { useState } from 'react'
import type { Etudiant } from '../api/types'

const CLE = 'presencepair.identite'

type Identite = { promotionId?: number; etudiant?: Etudiant }

function lire(): Identite {
  try {
    return JSON.parse(localStorage.getItem(CLE) ?? '{}') as Identite
  } catch {
    return {}
  }
}

/** Mémorise dans le navigateur le nom choisi (simple confort : il n'y a pas de connexion, Q1). */
export function useIdentite() {
  const [identite, setIdentite] = useState<Identite>(lire)

  // Mise à jour fonctionnelle : chaque modification part de l'état le plus récent,
  // jamais d'une valeur capturée au rendu précédent (bug #30).
  const modifier = (calculer: (actuelle: Identite) => Identite) => {
    setIdentite((actuelle) => {
      const suivante = calculer(actuelle)
      try {
        localStorage.setItem(CLE, JSON.stringify(suivante))
      } catch {
        // stockage indisponible (navigation privée) : l'identité reste en mémoire
      }
      return suivante
    })
  }

  return {
    promotionId: identite.promotionId,
    etudiant: identite.etudiant,
    /** Changer de promotion efface le nom choisi. */
    choisirPromotion: (promotionId: number | undefined) => modifier(() => ({ promotionId })),
    choisirEtudiant: (etudiant: Etudiant | undefined) => modifier((actuelle) => ({ ...actuelle, etudiant })),
  }
}
