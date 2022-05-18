import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function calendarSearchApi(searchValue, year, sort, searchBy, limit = 6, offset = 1) {
  let term = ''
  let yearTerm = '+year:' + year
  let includeActiveListOnlyTerm = '+AND+activeLists'
  let searchByAndValue = '../' + searchBy + ':' + searchValue

  if (year !== 'all') {
      term = term + yearTerm
  }
  if (searchValue !== '') {
    term = term + searchByAndValue
  }

  console.log(term)
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