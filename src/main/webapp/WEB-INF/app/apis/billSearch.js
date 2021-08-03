import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

// TODO add more search options for this endpoint
export default async function billSearch(term, session, limit = 6, offset = 1, sort = "_score:desc,session:desc") {
  const path = session ? `/api/3/bills/${session}/search?` : '/api/3/bills/search?'
  const response = await fetch(path + queryString.stringify({
    term: term,
    sort: sort,
    limit: limit,
    offset: offset
  }))
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
