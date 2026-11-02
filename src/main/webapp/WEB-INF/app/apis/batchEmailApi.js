import "core-js/stable";
import "regenerator-runtime/runtime";


export function BatchEmailBody(subscriptions, subject, body) {
  this.subscriptions = subscriptions
  this.subject = subject
  this.body = body
}

/** Sends the email to the signed-in admin only, resolving to the result once it has been sent. */
export function sendTestEmail(batchEmailBody) {
  return fetchUrl("/api/3/admin/email/testModeEmail", postOptions(batchEmailBody))
    .then((data) => data.result)
}

/** Starts sending the email to API users, resolving at once to the batch's status. */
export function startBatchEmail(batchEmailBody) {
  return fetchUrl("/api/3/admin/email/batchEmail", postOptions(batchEmailBody))
    .then((data) => data.result)
}

/** Resolves to the batch email being sent, or the last one sent, or null if there is none. */
export function getBatchEmailStatus() {
  return fetchUrl("/api/3/admin/email/batchEmail")
    .then((data) => data.result ?? null)
}

export function previewBatchEmail(batchEmailBody) {
  return fetchUrl("/api/3/admin/email/preview", postOptions(batchEmailBody))
    .then((data) => data.result.html)
}

function postOptions(batchEmailBody) {
  return {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(batchEmailBody),
  }
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
