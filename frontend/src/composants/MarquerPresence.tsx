import { useState } from 'react'
import type { FormEvent } from 'react'
import { ErreurApi } from '../api/client'
import { marquerPresence } from '../api/presences'
import { Etat } from './Etat'

/** EF3 : saisie du code sur téléphone (ENF1). Les règles restent côté serveur. */
export function MarquerPresence({ etudiantId }: { etudiantId: number }) {
  const [code, setCode] = useState('')
  const [enCours, setEnCours] = useState(false)
  const [erreur, setErreur] = useState<ErreurApi>()
  const [succes, setSucces] = useState(false)

  const soumettre = async (e: FormEvent) => {
    e.preventDefault()
    setEnCours(true)
    setErreur(undefined)
    setSucces(false)
    try {
      await marquerPresence(code, etudiantId)
      setSucces(true)
      setCode('')
    } catch (err) {
      setErreur(err as ErreurApi)
    } finally {
      setEnCours(false)
    }
  }

  return (
    <section>
      <h2>Marquer ma présence</h2>
      <form onSubmit={soumettre}>
        <label>
          Code affiché par le formateur{' '}
          <input
            value={code}
            onChange={(e) => setCode(e.target.value)}
            autoCapitalize="characters"
            autoComplete="off"
            inputMode="text"
            maxLength={10}
            placeholder="ex. K7MQ2B"
          />
        </label>
        <button type="submit" disabled={enCours || !code.trim()}>
          Je suis présent
        </button>
      </form>
      <Etat enCours={enCours} erreur={erreur} />
      {succes && (
        <p role="status" className="succes">
          Présence enregistrée.
        </p>
      )}
    </section>
  )
}
