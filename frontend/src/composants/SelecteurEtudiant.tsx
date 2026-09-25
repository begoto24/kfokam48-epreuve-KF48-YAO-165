import { listerEtudiants } from '../api/promotions'
import type { Etudiant } from '../api/types'
import { useChargement } from '../hooks/useChargement'
import { Etat } from './Etat'
import { SelecteurPromotion } from './SelecteurPromotion'

type Props = {
  etudiant: Etudiant | undefined
  promotionId: number | undefined
  onPromotion: (promotionId: number | undefined) => void
  onEtudiant: (etudiant: Etudiant | undefined) => void
}

/** EF2 : pas de mot de passe, l'étudiant choisit son nom dans la liste de sa promotion (Q1). */
export function SelecteurEtudiant({ etudiant, promotionId, onPromotion, onEtudiant }: Props) {
  const { donnees: etudiants, enCours, erreur } = useChargement(
    promotionId ? () => listerEtudiants(promotionId) : null,
    [promotionId],
  )

  return (
    <section>
      <h2>Qui es-tu ?</h2>
      <SelecteurPromotion
        promotionId={promotionId}
        onChange={onPromotion}
      />
      {promotionId && (
        <label>
          Ton nom{' '}
          <select
            value={etudiant?.id ?? ''}
            onChange={(e) => onEtudiant(etudiants?.find((x) => x.id === Number(e.target.value)))}
          >
            <option value="">— choisir —</option>
            {etudiants?.map((x) => (
              <option key={x.id} value={x.id}>
                {x.nom}
              </option>
            ))}
          </select>
        </label>
      )}
      <Etat enCours={enCours} erreur={erreur} />
    </section>
  )
}
