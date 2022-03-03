import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function calendarSearchApi(detail, from, to, order, type, limit = 6, offset = 1) {
  const response = await fetch(`/api/3/calendars/updates/` + from + '/' + to + '?' + queryString.stringify({
    detail: detail,
    limit: limit,
    offset: offset,
    order: order,
    type: type
  }))
  const data = await response.json()
  if (!data.success) {
    console.log('failed to get calendar updates api response')
    throw new Error(data.message)
  }
  return data
}