export default function getSessionApi(year, dateTime) {
  let url = "/api/3/transcripts"
  if (year)
    url += `/${lawId}`
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
  return data.result
}
