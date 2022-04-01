import * as queryString from "query-string";


export function getDataProcessLogs(from, to, searchParams) {
  let url = `/api/3/admin/process/runs/${from}/${to}?detail=true`
  if (searchParams) {
    url += "&" + queryString.stringify(searchParams)
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
