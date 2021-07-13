import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

// TODO add more search options for this endpoint
export default async function billSearch(term, limit = 6, offset = 1) {
  const response = await fetch(`/api/3/bills/search?` + queryString.stringify({
    term: term,
    // sort: "_score:desc,session:desc", // TODO does this work? is it working on prod?
    limit: limit,
    offset: offset
  }))
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}