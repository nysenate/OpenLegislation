import React from "react"
import {
  doClearIndex,
  doRebuildIndex,
  fetchIndexNames
} from "app/apis/indicesApi";
import LoadingIndicator from "app/shared/LoadingIndicator";


const ALL_INDICES = {
  name: "ALL",
  indexName: "all indices",
  primaryStore: false
}

export default function Indices({ setHeaderText }) {
  const [ indices, setIndices ] = React.useState([])
  const [ isRebuilding, setIsRebuilding ] = React.useState(false)

  React.useEffect(() => {
    setHeaderText("Manage Indices")
    fetchIndexNames()
      .then((res) => {
        // Filter out indices which are the primary store for their data.
        // We never want to accidentally clear them since they are the only source of their data.
        let indices = res.result.items.filter((i) => !i.primaryStore);

        indices.unshift(ALL_INDICES)
        setIndices(indices)
      })
  }, [])

  return (
    <div className="py-5 px-6 grid grid-cols-1 lg:grid-cols-3 gap-6">
      {indices.map((i) => <IndexCard index={i}
                                     isRebuilding={isRebuilding}
                                     setIsRebuilding={setIsRebuilding}
                                     key={i.name} />)}
    </div>
  )
}

function IndexCard({ index, isRebuilding, setIsRebuilding }) {
  const [ isLoading, setIsLoading ] = React.useState(false)

  const warmIndex = index => {
    setIsRebuilding(true)
    setIsLoading(true)
    doRebuildIndex(index)
      .finally(() => {
        setIsLoading(false)
        setIsRebuilding(false)
      })
  }

  return (
    <div className="card">
      <h3 className="card__title capitalize">{index.indexName}</h3>
      <div className={isLoading ? "visible" : "invisible"}>
        <LoadingIndicator />
      </div>
      <div className="mt-10 flex justify-evenly">
        <button className="btn btn--primary w-48"
                disabled={isRebuilding}
                onClick={() => warmIndex(index)}>
          Rebuild
        </button>
      </div>
    </div>
  )
}
