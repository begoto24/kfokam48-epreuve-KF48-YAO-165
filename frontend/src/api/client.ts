// Seul point d'entrée HTTP de l'application (F3) : aucun fetch ailleurs.

/** Erreur renvoyée par l'API au format imposé { code, message }. */
export class ErreurApi extends Error {
  readonly code: string
  readonly statut: number

  constructor(statut: number, code: string, message: string) {
    super(message)
    this.code = code
    this.statut = statut
  }
}

type Options = {
  methode?: 'GET' | 'POST' | 'PUT'
  corps?: unknown
  entetes?: Record<string, string>
}

export async function appeler<T>(chemin: string, options: Options = {}): Promise<T> {
  let reponse: Response
  try {
    reponse = await fetch(`/api${chemin}`, {
      method: options.methode ?? 'GET',
      headers: {
        ...(options.corps !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...options.entetes,
      },
      body: options.corps !== undefined ? JSON.stringify(options.corps) : undefined,
    })
  } catch {
    throw new ErreurApi(0, 'RESEAU', "Le serveur est injoignable. Vérifie que le backend est démarré.")
  }

  const texte = await reponse.text()
  const donnees = texte ? JSON.parse(texte) : undefined

  if (!reponse.ok) {
    const code = donnees?.code ?? 'ERREUR_INTERNE'
    const message = donnees?.message ?? `Erreur ${reponse.status}`
    throw new ErreurApi(reponse.status, code, message)
  }
  return donnees as T
}
