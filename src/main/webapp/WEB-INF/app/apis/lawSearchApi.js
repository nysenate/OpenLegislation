import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function lawSearchApi(term, limit = 6, offset = 1, sort = "_score:desc,session:desc") {
  const response = await fetch(`/api/3/laws/search?` + queryString.stringify({
    term: term,
    limit: limit,
    offset: offset
  }))
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}