import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function lawSearchApi(detail, from, to, withSelect, sortSelect, limit = 6, offset = 1) {
  const response = await fetch(`/api/3/laws/updates/` + from.toISO() + '/' + to.toISO() + '?' + queryString.stringify({
    detail: detail,
    limit: limit,
    offset: offset,
    order: sortSelect,
    type: withSelect
  }))
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}