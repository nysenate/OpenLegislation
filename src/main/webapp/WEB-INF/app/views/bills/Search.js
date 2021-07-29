import React from 'react';
import billSearch from "app/apis/billSearch";
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import SearchResults from "app/views/bills/SearchResults";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { billSessionYears } from "app/lib/dateUtils"

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

const defaultSession = "Any"

const billSessionYearEls = () => {
  return billSessionYears().map((y) => {
    return <option key={y} value={y}>{y}</option>
  })
}

/**
 * Triggers a bill search API call by the parent <Search> component whenever it updates a search param.
 */
function SearchForm() {
  const [ term, setTerm ] = React.useState("")
  const [ sort, setSort ] = React.useState(sortOptions.relevant)
  const [ session, setSession ] = React.useState(defaultSession)
  const location = useLocation()
  const history = useHistory()

  // Update search fields when back/forward navigation is used.
  React.useEffect(() => {
    const params = queryString.parse(location.search)
    setTerm(params.term || "")
    setSort(params.sort || sortOptions.relevant)
    setSession(params.session || defaultSession)
  }, [ location ])

  // Updates the term query param when the form is submitted.
  const onSubmit = (e) => {
    e.preventDefault()
    const params = queryString.parse(location.search)
    params.term = term
    params.sort = sort
    params.session = session
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div className="flex flex-wrap">
          <div className="flex-grow mr-8">
            <label htmlFor="billsearch" className="label label--top">
              Print number or term:
            </label>
            <input onChange={(e) => setTerm(e.target.value)}
                   value={term}
                   tabIndex="1"
                   name="billsearch"
                   type="text"
                   className="input w-full"
                   placeholder="e.g. S1234-2015 or yogurt" />
          </div>
          <div className="mr-8">
            <label htmlFor="session-year-select" className="label label--top">Session Year:</label>
            <select id="session-year-select"
                    tabIndex="2"
                    value={session}
                    onChange={(e) => setSession(e.target.value)}
                    className="select w-full">
              <option value="Any">Any</option>
              {billSessionYearEls()}
            </select>
          </div>
          <div className="">
            <label htmlFor="sort-by-select" className="label label--top">Sort By:</label>
            <select id="sort-by-select"
                    tabIndex="3"
                    value={sort}
                    onChange={(e) => setSort(e.target.value)}
                    className="select w-full">
              <option value={sortOptions.relevant}>Relevant</option>
              <option value={sortOptions.recentStatusUpdate}>Recent Status Update</option>
              <option value={sortOptions.printNo}>Print No</option>
              <option value={sortOptions.mostProgress}>Most Progress</option>
              <option value={sortOptions.mostAmendments}>Most Amendments</option>
            </select>
          </div>
        </div>

        <div className="flex justify-end">
          <button className="btn my-3 w-36" type="submit" tabIndex="4">Search</button>
        </div>
      </form>
    </div>
  )
}
