import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function calendarSearchApi(searchValue, activeListOnly, year, sort, searchBy, limit = 6, offset = 1) {
  let term = '+year:' + year
  let includeActiveListOnlyTerm = '+AND+activeLists'
  let searchByAndValue = searchBy + ':' + searchValue

  if (searchValue !== '') {
    term = term + searchByAndValue
  }
  if (activeListOnly) {
    term = term + includeActiveListOnlyTerm
  }
  const response = await fetch(`/api/3/calendars/search?` + queryString.stringify({
    term: term,
    sort: 'calDate:' + sort.toUpperCase(),
    limit: limit,
    offset: offset,
  }))
  const data = await response.json()
  if (!data.success) {
    console.log('failed to get calendar search api response')
    throw new Error(data.message)
  }
  return data
}