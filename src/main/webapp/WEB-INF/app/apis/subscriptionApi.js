export default function getSubscriptions(apiKey) {
  return fetchUrl(getBaseApi() + `/current?key=${apiKey}`)
}

export function setSubscriptions(apiKey, getBreakingChanges, getNewFeatures) {

}

/**
 * Changes the email of the given API user.
 * @returns {boolean} false is the email is already in use.
 */
export function setEmail(apiKey, email) {
  const checkEmailUrl = getBaseApi() + `/emailSearch?email=${email}`
  const emailInUse = fetchUrl(checkEmailUrl)[0]
  if (emailInUse) {
    return false
  }
  const setEmailUrl = getBaseApi() + `/updateEmail/?key=${apiKey}`
  fetchUrl(setEmailUrl, { method: "POST", body: email }).then()
  return true
}

function getBaseApi() {
  return "/api/3/email/subscription"
}

async function fetchUrl(url, options) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    let error = new Error(data.message)
    error.errorCode = data.errorCode
    throw error
  }
  return data
}
