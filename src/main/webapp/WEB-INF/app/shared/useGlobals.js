import React from "react"


/**
 * Creates a GlobalsContext which contains global config params.
 */
const GlobalsContext = React.createContext()

function useProvideGlobals() {
  const [ globals, setGlobals ] = React.useState({})

  React.useEffect(() => {
    fetchGlobals()
      .then((res) => setGlobals(res))
  }, [])

  return globals.result
}

export function GlobalsProvider({ children }) {
  const globals = useProvideGlobals()

  return (
    <GlobalsContext.Provider value={globals}>
      {children}
    </GlobalsContext.Provider>
  )
}

export default function useGlobals() {
  return React.useContext(GlobalsContext)
}

async function fetchGlobals() {
  const response = await fetch("/globals")
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}