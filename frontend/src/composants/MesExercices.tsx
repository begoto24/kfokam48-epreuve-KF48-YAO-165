import { listerMesExercices } from '../api/exercices'
import { useChargement } from '../hooks/useChargement'
import { Etat } from './Etat'
import { LIBELLE_STATUT_EXERCICE } from './libelles'

/** EF14 : note retenue et commentaires reçus, telles que l'API les renvoie (RG16, F3). */
export function MesExercices({ etudiantId }: { etudiantId: number }) {
  const exercices = useChargement(() => listerMesExercices(etudiantId), [etudiantId])

  return (
    <section>
      <h2>Mes exercices et mes notes</h2>
      <button onClick={exercices.recharger} disabled={exercices.enCours}>
        Actualiser
      </button>
      <Etat enCours={exercices.enCours} erreur={exercices.erreur} />
      {exercices.donnees?.length === 0 && <p>Aucun exercice déposé pour l’instant.</p>}
      <ul>
        {exercices.donnees?.map((e) => (
          <li key={e.id}>
            <strong>{e.sessionTitre}</strong> — {LIBELLE_STATUT_EXERCICE[e.statut]}
            <br />
            {e.note === null ? (
              'Pas encore de note.'
            ) : (
              <>
                Note : <strong>{e.note}/20</strong>
                {e.noteProvisoire && <em> (provisoire : une seule relecture rendue sur deux)</em>}
              </>
            )}
            {e.commentaires.length > 0 && (
              <ul>
                {e.commentaires.map((c, i) => (
                  <li key={i}>« {c} »</li>
                ))}
              </ul>
            )}
          </li>
        ))}
      </ul>
    </section>
  )
}
