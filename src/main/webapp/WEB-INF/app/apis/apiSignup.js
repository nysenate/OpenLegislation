import "core-js/stable";
import "regenerator-runtime/runtime";

export default async function apiSignup(name, email, subscriptions) {
  const response = await fetch('/register/signup', {
    method: 'POST',
    cache: 'no-cache',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      name: name,
      email: email,
      subscriptions: subscriptions
    })
  })
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}