import type { Etudiant } from '../api/types'

export function EcranEtudiant({ etudiant }: { etudiant: Etudiant }) {
  return (
    <section>
      <h2>Bonjour {etudiant.nom}</h2>
      <p>Marquer ta présence et déposer ton exercice.</p>
    </section>
  )
}
