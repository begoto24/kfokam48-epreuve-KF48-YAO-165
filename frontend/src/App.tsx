import { useState } from 'react'
import { SelecteurEtudiant } from './composants/SelecteurEtudiant'
import { EcranEtudiant } from './ecrans/EcranEtudiant'
import { EcranFormateur } from './ecrans/EcranFormateur'
import { EcranRelecteur } from './ecrans/EcranRelecteur'
import { useIdentite } from './hooks/useIdentite'

type Onglet = 'formateur' | 'etudiant' | 'relecteur'

const ONGLETS: { id: Onglet; libelle: string }[] = [
  { id: 'formateur', libelle: 'Formateur' },
  { id: 'etudiant', libelle: 'Étudiant' },
  { id: 'relecteur', libelle: 'Relecteur' },
]

/** Les trois écrans imposés (F2). Le formateur n'a pas d'identité à choisir. */
export default function App() {
  const [onglet, setOnglet] = useState<Onglet>('etudiant')
  const identite = useIdentite()

  return (
    <main>
      <h1>PrésencePair KFOKAM48</h1>
      <nav>
        {ONGLETS.map((o) => (
          <button key={o.id} aria-current={onglet === o.id ? 'page' : undefined} onClick={() => setOnglet(o.id)}>
            {o.libelle}
          </button>
        ))}
      </nav>

      {onglet === 'formateur' && <EcranFormateur />}

      {onglet !== 'formateur' && (
        <>
          <SelecteurEtudiant
            promotionId={identite.promotionId}
            etudiant={identite.etudiant}
            onPromotion={identite.choisirPromotion}
            onEtudiant={identite.choisirEtudiant}
          />
          {identite.etudiant && onglet === 'etudiant' && <EcranEtudiant etudiant={identite.etudiant} />}
          {identite.etudiant && onglet === 'relecteur' && <EcranRelecteur etudiant={identite.etudiant} />}
        </>
      )}
    </main>
  )
}
