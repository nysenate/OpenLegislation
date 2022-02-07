import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";


export function loginWithApiKey(apiKey) {
  const url = "/loginapikey"
  const options = {
    method: 'POST',
    cache: 'no-cache',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ apiKey: apiKey })
  }
  return fetchUrl(url, options)
}

export function loginAdmin(username, password) {
  const url = "/admin/login"
  const options = {
    method: "POST",
    cache: "no-cache",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username: username, password: password })
  }
  return fetchUrl(url, options)
}

export function logout() {
  const url = "/logout"
  const options = { method: "GET" }
  return fetchUrl(url, options)
}

async function fetchUrl(url, options = {}) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    let error = new Error(data.message)
    error.errorCode = data.errorCode
    throw error
  }
  return data
}
