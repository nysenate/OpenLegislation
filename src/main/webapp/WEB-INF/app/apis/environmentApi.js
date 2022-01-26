import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";


export function fetchEnvironmentVariables() {
  let url = `/api/3/admin/environment`
  return fetchUrl(url)
}

export function setEnvironmentVariable(name, value) {
  let url = `/api/3/admin/environment/set`
  url += "?" + queryString.stringify({varName: name, value: value})
  return fetchUrl(url)
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
