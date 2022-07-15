import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";


export function fetchMismatchTypes() {
  const url = "/api/3/admin/spotcheck/mismatch-types"
  return fetchUrl(url)
}

export function fetchMismatchHtmlDiff(id, searchParams) {
  let url = `/api/3/admin/spotcheck/mismatches/${id}/htmldiff`
  if (searchParams) {
    url += "?" + queryString.stringify(searchParams)
  }
  return fetchUrl(url)
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
