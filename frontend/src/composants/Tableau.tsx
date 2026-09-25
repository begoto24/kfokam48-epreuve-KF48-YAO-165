import { chargerTableau } from '../api/tableau'
import { useChargement } from '../hooks/useChargement'
import { Etat } from './Etat'

/**
 * EF8 : la moyenne est affichée telle que l'API la renvoie, jamais recalculée ici (F3, RG16).
 * « provisoire » vient aussi de l'API (moyenneProvisoire, contrat v1.2).
 */
export function Tableau({ promotionId }: { promotionId: number }) {
  const tableau = useChargement(() => chargerTableau(promotionId), [promotionId])

  return (
    <section>
      <h2>Tableau de la promotion</h2>
      <button onClick={tableau.recharger} disabled={tableau.enCours}>
        Actualiser
      </button>
      <Etat enCours={tableau.enCours} erreur={tableau.erreur} />
      {tableau.donnees && (
        <div className="tableau">
          <table>
            <thead>
              <tr>
                <th>Étudiant</th>
                <th>Présences</th>
                <th>Exercices déposés</th>
                <th>Moyenne reçue</th>
                <th>Relectures à faire</th>
              </tr>
            </thead>
            <tbody>
              {tableau.donnees.map((l) => (
                <tr key={l.etudiantId}>
                  <td>{l.nom}</td>
                  <td>{l.presences}</td>
                  <td>{l.exercicesDeposes}</td>
                  <td>
                    {l.moyenne ?? '—'}
                    {l.moyenneProvisoire && <em> (provisoire)</em>}
                  </td>
                  <td className={l.relecturesEnAttente > 0 ? 'erreur' : undefined}>{l.relecturesEnAttente}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
