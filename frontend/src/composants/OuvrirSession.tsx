import { useState } from 'react'
import type { FormEvent } from 'react'
import { ErreurApi } from '../api/client'
import { ouvrirSession } from '../api/sessions'
import type { SessionOuverte } from '../api/types'
import { Etat } from './Etat'

const heure = (iso: string) => new Date(iso).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })

/** EF1 : le formateur ouvre une session et affiche le code à la classe. */
export function OuvrirSession({ promotionId }: { promotionId: number }) {
  const [titre, setTitre] = useState('')
  const [session, setSession] = useState<SessionOuverte>()
  const [enCours, setEnCours] = useState(false)
  const [erreur, setErreur] = useState<ErreurApi>()

  const soumettre = async (e: FormEvent) => {
    e.preventDefault()
    setEnCours(true)
    setErreur(undefined)
    try {
      setSession(await ouvrirSession(titre, promotionId))
      setTitre('')
    } catch (err) {
      setErreur(err as ErreurApi)
    } finally {
      setEnCours(false)
    }
  }

  return (
    <section>
      <h2>Ouvrir une session</h2>
      <form onSubmit={soumettre}>
        <label>
          Titre du cours <input value={titre} onChange={(e) => setTitre(e.target.value)} maxLength={200} />
        </label>
        <button type="submit" disabled={enCours}>
          Ouvrir et obtenir un code
        </button>
      </form>
      <Etat enCours={enCours} erreur={erreur} />
      {session && (
        <div role="status">
          <p>Code de présence à afficher :</p>
          <p style={{ fontSize: '3rem', fontWeight: 'bold', letterSpacing: '0.3em' }}>{session.code}</p>
          <p>
            Valable jusqu'à <strong>{heure(session.expirationAt)}</strong> (ouvert à {heure(session.ouvertureAt)}).
          </p>
        </div>
      )}
    </section>
  )
}
