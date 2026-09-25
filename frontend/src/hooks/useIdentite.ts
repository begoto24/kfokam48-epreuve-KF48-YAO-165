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

  const modifier = (suivante: Identite) => {
    setIdentite(suivante)
    try {
      localStorage.setItem(CLE, JSON.stringify(suivante))
    } catch {
      // stockage indisponible (navigation privée) : l'identité reste en mémoire
    }
  }

  return {
    promotionId: identite.promotionId,
    etudiant: identite.etudiant,
    choisirPromotion: (promotionId: number | undefined) => modifier({ promotionId }),
    choisirEtudiant: (etudiant: Etudiant | undefined) => modifier({ promotionId: identite.promotionId, etudiant }),
  }
}
