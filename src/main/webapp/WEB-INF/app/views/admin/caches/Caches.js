import React from "react"
import {
  doClearCache,
  doWarmCache,
  fetchCacheStats,
} from "app/apis/cacheApi";
import LoadingIndicator from "app/shared/LoadingIndicator";


export default function Caches({ setHeaderText }) {
  const [ cacheNames, setCacheNames ] = React.useState([])

  React.useEffect(() => {
    setHeaderText("Manage Caches")
    fetchCacheStats()
      .then((res) => setCacheNames(res.result.items.map((c) => c.cacheName)))
  }, [])

  return (
    <div className="py-5 px-6 grid grid-cols-1 lg:grid-cols-3 gap-6">
      {cacheNames.map((name) => {
        return <CacheCard name={name} key={name} />
      })}
    </div>
  )
}

function CacheCard({ name }) {
  const [ cache, setCache ] = React.useState({})
  const [ isWarming, setIsWarming ] = React.useState(false)

  React.useEffect(() => {
    loadCacheStats()
  }, [ name ])

  /** While warming, fetch updated cache stats every 5 seconds. */
  React.useEffect(() => {
    let intervalId
    if (isWarming) {
      intervalId = setInterval(() => {
        loadCacheStats()
      }, 5000)
      return () => clearInterval(intervalId)
    }
  }, [ isWarming ])

  const loadCacheStats = () => {
    fetchCacheStats(name)
      .then((res) => setCache(res.result))
  }

  const warmCache = (cache) => {
    setIsWarming(true)
    doWarmCache(cache.cacheName)
      .then(() => {
        setIsWarming(false)
        loadCacheStats() // Ensure we have the most up-to-date cache stats.
      })
  }

  const clearCache = (cache) => {
    doClearCache(cache.cacheName)
      .then(() => loadCacheStats())
  }

  return (
    <div className="card">
      <h3 className="card__title">{cache.cacheName}</h3>
      <div className={isWarming ? "visible" : "invisible"}>
        <LoadingIndicator />
      </div>
      <div className="my-5">
        <ul>
          <li>Entries: {cache.size}</li>
          <li>Max size: {cache.capacity}</li>
          <li>Hits: {cache.hitCount}</li>
        </ul>
      </div>
      <div className="flex justify-evenly">
        <button className="btn btn--secondary w-24" onClick={() => clearCache(cache)}>Clear</button>
        <button className="btn btn--primary w-24" onClick={() => warmCache(cache)}>Warm</button>
      </div>
    </div>
  )
}
