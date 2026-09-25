import { useState } from 'react'
import type { FormEvent } from 'react'
import { ErreurApi } from '../api/client'
import { deposerExercice } from '../api/exercices'
import { listerSessions } from '../api/promotions'
import type { Etudiant, ExerciceDepose } from '../api/types'
import { useChargement } from '../hooks/useChargement'
import { Etat } from './Etat'
import { LIBELLE_STATUT_EXERCICE } from './libelles'

/** EF4 : l'étudiant dépose le lien de son exercice pour une session de sa promotion. */
export function DeposerExercice({ etudiant }: { etudiant: Etudiant }) {
  const sessions = useChargement(() => listerSessions(etudiant.promotionId), [etudiant.promotionId])
  const [sessionId, setSessionId] = useState<number>()
  const [lien, setLien] = useState('')
  const [enCours, setEnCours] = useState(false)
  const [erreur, setErreur] = useState<ErreurApi>()
  const [depot, setDepot] = useState<ExerciceDepose>()

  const soumettre = async (e: FormEvent) => {
    e.preventDefault()
    if (!sessionId) return
    setEnCours(true)
    setErreur(undefined)
    setDepot(undefined)
    try {
      setDepot(await deposerExercice(sessionId, etudiant.id, lien))
      setLien('')
    } catch (err) {
      setErreur(err as ErreurApi)
    } finally {
      setEnCours(false)
    }
  }

  return (
    <section>
      <h2>Déposer mon exercice</h2>
      <Etat enCours={sessions.enCours} erreur={sessions.erreur} />
      <form onSubmit={soumettre}>
        <label>
          Session{' '}
          <select value={sessionId ?? ''} onChange={(e) => setSessionId(Number(e.target.value) || undefined)}>
            <option value="">— choisir —</option>
            {sessions.donnees?.map((s) => (
              <option key={s.id} value={s.id}>
                {s.titre} — {new Date(s.ouvertureAt).toLocaleDateString('fr-FR')}
                {s.cloturee ? ' (clôturée)' : ''}
              </option>
            ))}
          </select>
        </label>
        <label>
          Lien de l’exercice{' '}
          <input
            type="url"
            value={lien}
            onChange={(e) => setLien(e.target.value)}
            placeholder="https://github.com/…"
            maxLength={500}
          />
        </label>
        <button type="submit" disabled={enCours || !sessionId || !lien.trim()}>
          Déposer
        </button>
      </form>
      <Etat enCours={enCours} erreur={erreur} />
      {depot && (
        <p role="status" className="succes">
          Exercice déposé : {LIBELLE_STATUT_EXERCICE[depot.statut]}.
        </p>
      )}
    </section>
  )
}
