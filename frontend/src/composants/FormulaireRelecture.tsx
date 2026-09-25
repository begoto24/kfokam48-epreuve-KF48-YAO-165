import { useState } from 'react'
import type { FormEvent } from 'react'
import { ErreurApi } from '../api/client'
import { rendreRelecture } from '../api/relectures'
import type { Relecture } from '../api/types'
import { Etat } from './Etat'

type Props = {
  relecture: Relecture
  etudiantId: number
  onRendue: () => void
}

/** EF7 : note et commentaire. Les bornes et l'entier sont vérifiés par l'API (RG9), pas ici. */
export function FormulaireRelecture({ relecture, etudiantId, onRendue }: Props) {
  const [note, setNote] = useState(relecture.note?.toString() ?? '')
  const [commentaire, setCommentaire] = useState(relecture.commentaire ?? '')
  const [enCours, setEnCours] = useState(false)
  const [erreur, setErreur] = useState<ErreurApi>()

  const rendre = async (e: FormEvent) => {
    e.preventDefault()
    if (!window.confirm('Une relecture rendue est définitive. Confirmer ?')) return
    setEnCours(true)
    setErreur(undefined)
    try {
      await rendreRelecture(relecture.id, etudiantId, Number(note), commentaire)
      onRendue()
    } catch (err) {
      setErreur(err as ErreurApi)
    } finally {
      setEnCours(false)
    }
  }

  return (
    <form onSubmit={rendre}>
      <label>
        Note sur 20 <input type="number" value={note} onChange={(e) => setNote(e.target.value)} />
      </label>
      <label>
        Commentaire
        <br />
        <textarea value={commentaire} onChange={(e) => setCommentaire(e.target.value)} rows={3} maxLength={2000} />
      </label>
      <button type="submit" disabled={enCours || note === '' || !commentaire.trim()}>
        Rendre définitivement
      </button>
      <Etat enCours={enCours} erreur={erreur} />
    </form>
  )
}
