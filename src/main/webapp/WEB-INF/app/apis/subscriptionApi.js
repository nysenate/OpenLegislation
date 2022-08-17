const baseApi = "/api/3/email/subscription"

export default function getSubscriptions(apiKey) {
  return fetchJson(`/current?key=${apiKey}`)
}

export function updateSubscriptions(apiKey, subs) {
  const options = {
    method: "POST",
    cache: "no-cache",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(subs)
  }
  return fetchJson(`/update?key=${apiKey}`, options)
}

export function getEmail(apiKey) {
  return fetchJson(`/getEmail?key=${apiKey}`)
}

/**
 * Changes the email of the given API user.
 * @returns [true] if the email is already in use.
 */
export function updateEmail(apiKey, email) {
  return fetchJson(`/updateEmail?key=${apiKey}`, { method: "POST", body: email })
}

async function fetchJson(urlSuffix, options = {method: "GET"}) {
  const response = await fetch(baseApi + urlSuffix, options)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
