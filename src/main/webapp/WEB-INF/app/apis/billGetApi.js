import "core-js/stable";
import "regenerator-runtime/runtime";

/**
 *
 * @param sessionYear
 * @param printNo
 * @param view The level of detail to return. See BillGetCtrl.getBill for more details.
 * @returns {Promise<*>}
 */
export function getBillApi(sessionYear, printNo, view) {
  let url = `/api/3/bills/${sessionYear}/${printNo}`
  if (view) {
    url += `?view=${view}`
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
