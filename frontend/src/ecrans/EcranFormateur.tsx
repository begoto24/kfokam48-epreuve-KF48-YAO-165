import { useState } from 'react'
import { OuvrirSession } from '../composants/OuvrirSession'
import { SelecteurPromotion } from '../composants/SelecteurPromotion'
import { Tableau } from '../composants/Tableau'

/** F2 : ouvrir une session et voir le tableau. */
export function EcranFormateur() {
  const [promotionId, setPromotionId] = useState<number>()

  return (
    <>
      <section>
        <h2>Formateur</h2>
        <SelecteurPromotion promotionId={promotionId} onChange={setPromotionId} />
      </section>
      {promotionId && (
        <>
          <OuvrirSession promotionId={promotionId} />
          <Tableau promotionId={promotionId} />
        </>
      )}
    </>
  )
}
