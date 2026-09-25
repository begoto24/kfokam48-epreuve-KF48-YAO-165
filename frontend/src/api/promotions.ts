import { appeler } from './client'
import type { Etudiant, Promotion, Session } from './types'

export const listerPromotions = () => appeler<Promotion[]>('/promotions')

export const listerEtudiants = (promotionId: number) =>
  appeler<Etudiant[]>(`/promotions/${promotionId}/etudiants`)

export const listerSessions = (promotionId: number) => appeler<Session[]>(`/promotions/${promotionId}/sessions`)
