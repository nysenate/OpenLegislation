import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";
import { PageParams } from "app/shared/Pagination";


export function getCalendarUpdates({from, to, type, detail, sort, page, limit}) {
  from = from.startOf("day")
  to = to.endOf("day")
  const pageParams = new PageParams(page, limit)
  const searchParams = queryString.stringify({
    type: type,
    detail: detail,
    order: sort,
    limit: pageParams.limit,
    offset: pageParams.offset,
  })

  const url = `/api/3/calendars/updates/${from.toISO()}/${to.toISO()}?${searchParams}`
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
