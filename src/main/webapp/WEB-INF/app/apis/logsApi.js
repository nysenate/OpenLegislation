import * as queryString from "query-string";


export function getDataProcessLogs(from, to, searchParams) {
  let url = `/api/3/admin/process/runs/${from}/${to}?detail=true`
  if (searchParams) {
    url += "&" + queryString.stringify(searchParams)
  }
  return fetchUrl(url)
}

export function searchApiLogs(term, from, to, sort, limit, offset) {
  let url = `/api/3/admin/apiLogs?`
  const searchParams = {
    term: `(${term}) AND requestTime:[${from.toISO()} TO ${to.toISO()}]`,
    limit: limit,
    offset: offset,
    sort: sort
  }
  url += queryString.stringify(searchParams)
  return fetchUrl(url)
}

export function runDataProcess() {
  const url = "/api/3/admin/process/run"
  const options = { method: "POST" }
  return fetchUrl(url, options)
}

async function fetchUrl(url, options) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
