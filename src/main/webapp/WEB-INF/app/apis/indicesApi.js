import "core-js/stable";
import "regenerator-runtime/runtime";


export function fetchIndexNames() {
  const url = "/api/3/admin/index"
  return fetchUrl(url)
}

export function doClearIndex(index) {
  const url = `/api/3/admin/index/${index.name}`
  const options = {
    method: "DELETE"
  }
  return fetchUrl(url, options)
}

export function doRebuildIndex(index) {
  const url = `/api/3/admin/index/${index.name}`
  const options = {
    method: "PUT"
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
