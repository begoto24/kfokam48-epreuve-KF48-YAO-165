import type { Etudiant } from '../api/types'
import { DeposerExercice } from '../composants/DeposerExercice'
import { MarquerPresence } from '../composants/MarquerPresence'
import { MesExercices } from '../composants/MesExercices'

export function EcranEtudiant({ etudiant }: { etudiant: Etudiant }) {
  return (
    <>
      <h2>Bonjour {etudiant.nom}</h2>
      <MarquerPresence etudiantId={etudiant.id} />
      <DeposerExercice etudiant={etudiant} />
      <MesExercices etudiantId={etudiant.id} />
    </>
  )
}
