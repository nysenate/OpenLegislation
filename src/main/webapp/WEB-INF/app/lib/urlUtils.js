import * as queryString from "query-string";
import {
  useHistory,
  useLocation
} from "react-router-dom";


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

/**
 * Returns a query parameter value and a setter function that updates
 * the URL search string without losing other existing params.
 *
 * @param {string} key - The query parameter to read and write
 */
export function useQueryParam(key) {
  const location = useLocation()
  const history = useHistory()
  const value = queryString.parse(location.search)[key] || undefined

  const setValue = (newValue) => {
    const params = queryString.parse(location.search)
    params[key] = newValue
    history.replace({ search: queryString.stringify(params) })
  }

  return [ value, setValue ]
}