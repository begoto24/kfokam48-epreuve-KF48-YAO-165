import { useEffect, useState } from 'react'
import { ErreurApi } from '../api/client'

export type Chargement<T> = {
  donnees: T | undefined
  enCours: boolean
  erreur: ErreurApi | undefined
  recharger: () => void
}

/**
 * Charge une ressource et expose ses états de chargement et d'erreur (F3).
 * `requete` à null : rien à charger (ex. aucune promotion choisie).
 * La requête est relancée quand une valeur de `dependances` change, ou via `recharger()`.
 */
export function useChargement<T>(requete: (() => Promise<T>) | null, dependances: unknown[]): Chargement<T> {
  const [donnees, setDonnees] = useState<T>()
  const [enCours, setEnCours] = useState(false)
  const [erreur, setErreur] = useState<ErreurApi>()
  const [version, setVersion] = useState(0)

  useEffect(() => {
    if (!requete) {
      setDonnees(undefined)
      return
    }
    let annule = false
    setEnCours(true)
    setErreur(undefined)
    requete()
      .then((resultat) => {
        if (!annule) setDonnees(resultat)
      })
      .catch((e: unknown) => {
        if (!annule) setErreur(e instanceof ErreurApi ? e : new ErreurApi(0, 'INCONNUE', String(e)))
      })
      .finally(() => {
        if (!annule) setEnCours(false)
      })
    return () => {
      annule = true
    }
    // La requête est recréée à chaque rendu : on ne relance que sur les dépendances déclarées.
  }, [...dependances, version]) // eslint-disable-line react-hooks/exhaustive-deps

  return { donnees, enCours, erreur, recharger: () => setVersion((v) => v + 1) }
}
