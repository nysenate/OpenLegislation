import React from 'react';
import billSearch from "app/apis/billSearch";
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import SearchResults from "app/views/bills/SearchResults";
import LoadingIndicator from "app/shared/LoadingIndicator";

export default function Search() {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const limit = 6

  React.useEffect(() => {
    search()
  }, [ location ])

  // Perform a search using the query string parameters.
  const search = () => {
    const params = queryString.parse(location.search)
    const term = params.term || '*'
    const page = params.page || 1
    const offset = (page - 1) * limit + 1
    const sort = params.sort
    doSearch(term, limit, offset, sort)
  }

  const doSearch = (term, limit, offset, sort) => {
    setLoading(true)
    billSearch(term, limit, offset, sort)
      .then((response) => {
        setResponse(response)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`)
      })
      .finally(() => {
        setLoading(false)
      })
  }

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="p-3">
      <SearchForm searchTerm={params.term} />
      {loading &&
      <LoadingIndicator />
      }
      {!loading &&
      <SearchResults response={response}
                     limit={limit}
                     page={params.page}
                     onPageChange={onPageChange} />
      }
    </div>
  )
}

const sortOptions = {
  relevant: "_score:desc,session:desc",
  recentStatusUpdate: "status.actionDate:desc,_score:desc",
  printNo: "printNo:asc,session:desc",
  mostProgress: "milestones.size:desc,_score:desc",
  mostAmendments: "amendments.size:desc,_score:desc",
}

/**
 * Triggers a bill search API call by the parent <Search> component whenever it updates a search param.
 */
function SearchForm() {
  const [ term, setTerm ] = React.useState("")
  const [ sort, setSort ] = React.useState(sortOptions.relevant)
  const location = useLocation()
  const history = useHistory()

  // Update search fields when back/forward navigation is used.
  React.useEffect(() => {
    const params = queryString.parse(location.search)
      setTerm(params.term || "")
      setSort(params.sort)
  }, [ location ])

  // Updates the term query param when the form is submitted.
  const onSubmit = (e) => {
    e.preventDefault()
    const params = queryString.parse(location.search)
    params.term = term
    history.push({ search: queryString.stringify(params) })
  }

  const onSortChange = (e) => {
    const params = queryString.parse(location.search)
    params.sort = e.target.value
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div>
          <label htmlFor="billsearch">
            Search for legislation by print number or term:
          </label>
        </div>
        <div className="flex items-baseline">
          <input onChange={(e) => setTerm(e.target.value)}
                 value={term}
                 tabIndex="1"
                 name="billsearch"
                 type="text"
                 className="input ml-2 mr-8 flex-grow"
                 placeholder="e.g. S1234-2015 or yogurt" />
          <button className="btn my-3 w-28 md:w-36" type="submit">Search</button>
        </div>
      </form>
      <div>
        <label htmlFor="sort-by-select">Sort By:</label>
        <select id="sort-by-select" value={sort} onChange={onSortChange}>
          <option value={sortOptions.relevant}>Relevant</option>
          <option value={sortOptions.recentStatusUpdate}>Recent Status Update</option>
          <option value={sortOptions.printNo}>Print No</option>
          <option value={sortOptions.mostProgress}>Most Progress</option>
          <option value={sortOptions.mostAmendments}>Most Amendments</option>
        </select>
      </div>
    </div>
  )
}
