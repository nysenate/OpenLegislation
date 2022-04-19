import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function agendaSearchApi(detail, from, to, order, type, limit = 6, offset = 1) {
  console.log(`/api/3/agendas/updates/` + from + '/' + to + '?' + queryString.stringify({
    detail: detail,
    limit: limit,
    offset: offset,
    order: order,
    type: type
  }))

  const response = await fetch(`/api/3/agendas/updates/` + from + '/' + to + '?' + queryString.stringify({
    detail: detail,
    limit: limit,
    offset: offset,
    order: order,
    type: type
  }))
  const data = await response.json()
  if (!data.success) {
    console.log('failed to get agenda updates api response')
    throw new Error(data.message)
  }
  return data
}