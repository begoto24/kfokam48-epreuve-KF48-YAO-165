import type { Etudiant } from '../api/types'
import { MarquerPresence } from '../composants/MarquerPresence'

export function EcranEtudiant({ etudiant }: { etudiant: Etudiant }) {
  return (
    <>
      <h2>Bonjour {etudiant.nom}</h2>
      <MarquerPresence etudiantId={etudiant.id} />
    </>
  )
}
