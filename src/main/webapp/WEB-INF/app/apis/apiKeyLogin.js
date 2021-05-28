import "core-js/stable";
import "regenerator-runtime/runtime";

export async function apiKeyLogin(apiKey) {
  const response = await fetch(`/loginapikey`, {
    method: 'POST',
    cache: 'no-cache',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({apiKey: apiKey})
  });
  const data = await response.json();
  if (!data.success) {
    throw new Error(data.message)
  }
  return data;
}

