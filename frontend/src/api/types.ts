// Types des réponses, recopiés de api/contrat.yaml (components/schemas).

export type Promotion = {
  id: number
  nom: string
}

export type Etudiant = {
  id: number
  nom: string
  promotionId: number
}
