import { appeler } from './client'
import type { Presence } from './types'

export const marquerPresence = (code: string, etudiantId: number) =>
  appeler<Presence>('/presences', { methode: 'POST', corps: { code, etudiantId } })
