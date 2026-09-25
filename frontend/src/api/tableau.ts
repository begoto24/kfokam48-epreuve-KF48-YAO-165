import { appeler } from './client'
import type { LigneTableau } from './types'

export const chargerTableau = (promotionId: number) => appeler<LigneTableau[]>(`/tableau?promotionId=${promotionId}`)
