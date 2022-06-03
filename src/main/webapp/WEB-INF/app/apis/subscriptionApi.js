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
  post(`/update?key=${apiKey}`, options)
}

export function getEmail(apiKey) {
  return fetchJson(`/getEmail?key=${apiKey}`)
}

export function emailInUse(email) {
  return fetchJson(`/emailSearch?email=${email}`)
}

/**
 * Changes the email of the given API user.
 * @returns {boolean} false if the email is already in use.
 */
export function updateEmail(apiKey, email) {
  post(`/updateEmail?key=${apiKey}`, { method: "POST", body: email })
}

// Functions from ApiUserEmailSubscriptionCtrl do not return BaseResponses
async function fetchJson(urlSuffix, options = {method: "GET"}) {
  const response = await fetch(baseApi + urlSuffix, options)
  return await response.json()
}

function post(urlSuffix, options) {
  fetch(baseApi + urlSuffix, options).then()
}
