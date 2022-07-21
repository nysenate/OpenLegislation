import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";


export function fetchMismatchTypes() {
  const url = "/api/3/admin/spotcheck/mismatch-types"
  return fetchUrl(url)
}

export function fetchMismatchTypeCounts(datasource, date, contentType, status) {
  const url = `/api/3/admin/spotcheck/mismatches/summary/mismatchtype` +
    `?contentType=${contentType}&datasource=${datasource}&mismatchStatus=${status}&reportDate=${date.toISODate()}`
  return fetchUrl(url)
}

export function fetchReferenceTypes() {
  const url = "/api/3/admin/spotcheck/reference-types"
  return fetchUrl(url)
}

export function fetchContentTypeCounts(datasource, date) {
  const url = `/api/3/admin/spotcheck/mismatches/summary/contenttype?datasource=${datasource}&reportDate=${date.toISODate()}`
  return fetchUrl(url)
}

export function fetchMismatchStatusCounts(datasource, date, contentType) {
  const url = `/api/3/admin/spotcheck/mismatches/summary/status?contentType=${contentType}&datasource=${datasource}&reportDate=${date.toISODate()}`
  return fetchUrl(url)
}

export function fetchMismatches(datasource, date, contentType, mismatchStatus, mismatchType, pageParams) {
  const url = `/api/3/admin/spotcheck/mismatches` +
    `?contentType=${contentType}&datasource=${datasource}&mismatchStatus=${mismatchStatus}` +
    `&mismatchType=${mismatchType}&reportDate=${date.toISODate()}` +
    `&limit=${pageParams.limit}&offset=${pageParams.offset}&orderBy=OBSERVED_DATE&sort=DESC`
  return fetchUrl(url)
}

export function fetchMismatchHtmlDiff(id, searchParams) {
  let url = `/api/3/admin/spotcheck/mismatches/${id}/htmldiff`
  if (searchParams) {
    url += "?" + queryString.stringify(searchParams)
  }
  return fetchUrl(url)
}

export function ignoreMismatchApi(mismatchId) {
  const url = `/api/3/admin/spotcheck/mismatches/${mismatchId}/ignore?ignoreLevel=IGNORE_UNTIL_RESOLVED`
  return fetchUrl(url, {method: "POST"})
}

async function fetchUrl(url, options) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
