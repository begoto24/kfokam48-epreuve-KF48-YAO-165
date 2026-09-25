import type { Etudiant } from '../api/types'

export function EcranRelecteur({ etudiant }: { etudiant: Etudiant }) {
  return (
    <section>
      <h2>Relectures de {etudiant.nom}</h2>
    </section>
  )
}
