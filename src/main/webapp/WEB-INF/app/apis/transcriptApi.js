import * as queryString from "query-string";

export default function transcriptApi(isHearing, year, pageNum, searchTerm) {
  const defaultLimit = 25
  let url = getBaseApi(isHearing)
  if (year)
    url += `/${year}`
  if (searchTerm)
    url += "/search"
  url += "?" + queryString.stringify({
    offset: (pageNum - 1) * defaultLimit + 1,
    term: searchTerm,
    summary: isHearing
  })
  console.log(url)
  return fetchUrl(url)
}

export function getTranscript(isHearing, id) {
  return fetchUrl(getBaseApi(isHearing) + `/${id}`)
}

function getBaseApi(isHearing) {
  return "/api/3/" + (isHearing ? "hearings" : "transcripts")
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data;
}
