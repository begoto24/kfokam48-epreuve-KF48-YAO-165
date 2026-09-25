import { appeler } from './client'
import type { Etudiant, Promotion } from './types'

export const listerPromotions = () => appeler<Promotion[]>('/promotions')

export const listerEtudiants = (promotionId: number) =>
  appeler<Etudiant[]>(`/promotions/${promotionId}/etudiants`)
