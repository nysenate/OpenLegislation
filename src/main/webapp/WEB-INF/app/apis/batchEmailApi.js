import "core-js/stable";
import "regenerator-runtime/runtime";


export function BatchEmailBody(subscriptions, subject, body) {
  this.subscriptions = subscriptions
  this.subject = subject
  this.body = body
}

export function sendBatchEmail(isTest = true, batchEmailBody) {
  const url = isTest ? "/api/3/admin/email/testModeEmail" : "/api/3/admin/email/batchEmail"
  const options = {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(batchEmailBody),
  }
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
