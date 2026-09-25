import { listerRelectures } from '../api/relectures'
import type { Etudiant } from '../api/types'
import { Etat } from '../composants/Etat'
import { FormulaireRelecture } from '../composants/FormulaireRelecture'
import { LIBELLE_STATUT_RELECTURE } from '../composants/libelles'
import { useChargement } from '../hooks/useChargement'

/** EF6, EF7 : les exercices à relire et la relecture elle-même. */
export function EcranRelecteur({ etudiant }: { etudiant: Etudiant }) {
  const relectures = useChargement(() => listerRelectures(etudiant.id), [etudiant.id])

  return (
    <section>
      <h2>Relectures de {etudiant.nom}</h2>
      <Etat enCours={relectures.enCours} erreur={relectures.erreur} />
      {relectures.donnees?.length === 0 && <p>Aucune relecture ne t’est assignée pour l’instant.</p>}
      <ul>
        {relectures.donnees?.map((r) => (
          <li key={r.id}>
            <strong>{r.sessionTitre}</strong> — {LIBELLE_STATUT_RELECTURE[r.statut]}
            {r.sessionCloturee && ' (session clôturée)'}
            <br />
            <a href={r.lien} target="_blank" rel="noreferrer">
              {r.lien}
            </a>
            {r.statut === 'RENDUE' ? (
              <p>
                Note rendue : {r.note}/20 — {r.commentaire}
              </p>
            ) : (
              !r.sessionCloturee && (
                <FormulaireRelecture relecture={r} etudiantId={etudiant.id} onRendue={relectures.recharger} />
              )
            )}
          </li>
        ))}
      </ul>
    </section>
  )
}
