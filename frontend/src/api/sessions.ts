import { appeler } from './client'
import type { SessionOuverte } from './types'

export const ouvrirSession = (titre: string, promotionId: number) =>
  appeler<SessionOuverte>('/sessions', { methode: 'POST', corps: { titre, promotionId } })
