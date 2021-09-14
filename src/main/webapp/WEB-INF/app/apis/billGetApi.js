import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

/**
 *
 * @param sessionYear
 * @param printNo
 * @param searchParams An object containing search parameter to set on api request. i.e. {view: "info"}
 * @returns {Promise<*>}
 */
export function getBillApi(sessionYear, printNo, searchParams) {
  let url = `/api/3/bills/${sessionYear}/${printNo}`
  if (searchParams) {
    url += "?" + queryString.stringify(searchParams)
  }
  return fetchUrl(url)
}

export function getBillsApi(sessionYear) {
  return fetchUrl(`/api/3/bills/${sessionYear}`)
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data.result
}

export function getBillStatusTypes() {
  return fetchUrl('/api/3/bills/status-types')
}
