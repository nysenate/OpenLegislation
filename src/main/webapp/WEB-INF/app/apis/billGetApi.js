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

/**
 * Get updates for a single bill.
 */
export function fetchSingleBillUpdates(sessionYear, printNo, searchParams) {
  let url = `/api/3/bills/${sessionYear}/${printNo}/updates`
  if (searchParams) {
    url += "?" + queryString.stringify(searchParams)
  }
  return fetchUrl(url)
}

/**
 * Get updates for all bills which match the supplied parameters.
 * @param {Object} params
 * @param {string} params.from - From date time ISO string, i.e. 2021-06-01T00:00:00.000
 * @param {string} params.to - To date time ISO string, ie. 2021-10-20T23:59:59.999
 * @param {Object} params.searchParams - Additional search parameters to set when calling the API.
 */
export function fetchAllBillUpdates({ from, to, ...searchParams }) {
  searchParams.summary = true; // Always send the summary=true search param so we get the correct response.
  let url = `/api/3/bills/updates/${from}/${to}`
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
  return data
}

export function getBillStatusTypes() {
  return fetchUrl('/api/3/bills/status-types')
}
