import type { ErreurApi } from '../api/client'

/** Affichage uniforme des états de chargement et d'erreur (F3). */
export function Etat({ enCours, erreur }: { enCours?: boolean; erreur?: ErreurApi }) {
  if (enCours) {
    return <p role="status">Chargement…</p>
  }
  if (erreur) {
    return (
      <p role="alert" className="erreur">
        {erreur.message} <small>({erreur.code})</small>
      </p>
    )
  }
  return null
}
