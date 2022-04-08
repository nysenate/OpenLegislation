

export function changePassword(newPassword) {
  const url = `/api/3/admin/accounts/passchange?password=${newPassword}`
  const options = {
    method: "POST"
  }
  return fetchUrl(url, options)
}

async function fetchUrl(url, options) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
