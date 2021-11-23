export default function getTranscriptApi(isHearing, year, id, getResult) {
  let url = "/api/3/"
  url += isHearing ? "hearings" : "transcripts";
  if (year)
    url += `/${year}`
  else if (id)
    url += `/${id}`
  return fetchUrl(url, getResult)
}

async function fetchUrl(url, getResult) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  // TODO: better variable name?
  if (getResult)
    return data.result
  return data;
}
