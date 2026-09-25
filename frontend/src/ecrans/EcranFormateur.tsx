import { useState } from 'react'
import { OuvrirSession } from '../composants/OuvrirSession'
import { SelecteurPromotion } from '../composants/SelecteurPromotion'

export function EcranFormateur() {
  const [promotionId, setPromotionId] = useState<number>()

  return (
    <>
      <section>
        <h2>Formateur</h2>
        <SelecteurPromotion promotionId={promotionId} onChange={setPromotionId} />
      </section>
      {promotionId && <OuvrirSession promotionId={promotionId} />}
    </>
  )
}
