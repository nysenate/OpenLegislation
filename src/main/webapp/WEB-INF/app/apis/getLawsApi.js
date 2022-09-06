import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default function getLawsApi(lawId, locationId, searchParams) {
  let url = `/api/3/laws`
  if (lawId) {
    url += `/${lawId}`
  }
  if (locationId) {
    url += `/${locationId}`
  }
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
  return data.result
}