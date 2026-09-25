import { listerPromotions } from '../api/promotions'
import { useChargement } from '../hooks/useChargement'
import { Etat } from './Etat'

type Props = {
  promotionId: number | undefined
  onChange: (promotionId: number | undefined) => void
}

export function SelecteurPromotion({ promotionId, onChange }: Props) {
  const { donnees: promotions, enCours, erreur } = useChargement(listerPromotions, [])

  return (
    <>
      <label>
        Promotion{' '}
        <select
          value={promotionId ?? ''}
          onChange={(e) => onChange(e.target.value ? Number(e.target.value) : undefined)}
        >
          <option value="">— choisir —</option>
          {promotions?.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nom}
            </option>
          ))}
        </select>
      </label>
      <Etat enCours={enCours} erreur={erreur} />
    </>
  )
}
