import * as queryString from "query-string";


/**
 * Returns a new string which is a copy of the given url except that
 * the "key" search parameter is removed if it exists.
 *
 * Does not modify the original url.
 */
export function anonymousUrl(url) {
  const searchParamStartIndex = url.indexOf("?")
  if (searchParamStartIndex === -1) {
    return url
  }
  const endpoint = url.slice(0, searchParamStartIndex)
  let params = queryString.parse(url.slice(searchParamStartIndex + 1))
  delete params.key
  return endpoint + "?" + queryString.stringify(params)
}
