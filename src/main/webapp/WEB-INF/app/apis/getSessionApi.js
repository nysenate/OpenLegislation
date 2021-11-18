export default function getSessionApi(year, dateTime) {
  let url = "/api/3/transcripts"
  if (year)
    url += `/${year}`
  else if (dateTime)
    url += `/${dateTime}`
  return fetchUrl(url)
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  // Gets the actual JSON data.
  return data.result
}
