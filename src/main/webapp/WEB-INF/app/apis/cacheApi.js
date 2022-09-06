import "core-js/stable";
import "regenerator-runtime/runtime";


/**
 * Fetch cache stats for a single cache or for all caches.
 *
 * @param name Get stats for the cache with this name. If omitted, fetch stats for all caches.
 */
export function fetchCacheStats(name) {
  const url = name ? `/api/3/admin/cache/${name}` : `/api/3/admin/cache`
  return fetchUrl(url)
}

export function doWarmCache(name) {
  const url = `/api/3/admin/cache/${name}`
  const options = {
    method: "PUT"
  }
  return fetchUrl(url, options)
}

export function doClearCache(name) {
  const url = `/api/3/admin/cache/${name}`
  const options = {
    method: "DELETE"
  }
  return fetchUrl(url, options)
}

async function fetchUrl(url, options = {}) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
