import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";


export function hasPermission(permission) {
  let url = `/api/3/auth/permission/check`
  if (permission) {
    url += "?" + queryString.stringify({permission: permission})
  }
  return fetchUrl(url)
    .then((res) => res.result.isPermitted)
}

export function loginWithApiKey(apiKey) {
  const url = "/loginapikey"
  const options = {
    method: 'POST',
    cache: 'no-cache',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({apiKey: apiKey})
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
