import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

// TODO add more search options for this endpoint
export default async function billSearch(term, limit = 6, offset = 1, sort = "_score:desc,session:desc") {
  const response = await fetch(`/api/3/bills/search?` + queryString.stringify({
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

const sortByOptions = {

}