import * as queryString from "query-string";

export default function getTranscript(isHearing, id) {
  return fetchUrl(getBaseApi(isHearing) + `/${id}`)
}

export function transcriptSearchApi(isHearing, year, limit, offset) {
  let url = getBaseApi(isHearing)
  if (year)
    url += `/${year}`
  url += "?" + queryString.stringify({
    limit: limit, offset: offset
  })
  return fetchUrl(url)
}

function getBaseApi(isHearing) {
  return "/api/3/" + (isHearing ? "hearings" : "transcripts");
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data;
}
