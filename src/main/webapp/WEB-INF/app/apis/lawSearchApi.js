import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";


export default async function lawSearchApi(term, pageParams, sort = "_score:desc,session:desc") {
  const response = await fetch(`/api/3/laws/search?` + queryString.stringify({
    term: term,
    limit: pageParams.limit,
    offset: pageParams.offset
  }))
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}