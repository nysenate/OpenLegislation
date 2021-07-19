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

  // When the url changes, perform a new search using the term from the query string.
  React.useEffect(() => {
    const term = params.term || '*'
    const page = params.page || 1
    const offset = (page - 1) * limit + 1
    doSearch(term, limit, offset)
  }, [ location ])

  // Updates the queryString with the new search term and restarts at page 1.
  const submitSearch = term => {
    params.term = term
    params.page = 1
    history.push({ search: queryString.stringify(params) })
  }

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({ search: queryString.stringify(params) })
  }

  const doSearch = (term, limit, offset) => {
    setLoading(true)
    billSearch(term, limit, offset)
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

  return (
    <div className="p-3">
      <SearchForm searchTerm={params.term} submitSearch={submitSearch} />
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

function SearchForm({ searchTerm = "", submitSearch }) {
  const [ term, setTerm ] = React.useState(searchTerm)

  React.useEffect(() => {
    setTerm(searchTerm)
  }, [ searchTerm ])

  const handleSubmit = (e) => {
    e.preventDefault();
    submitSearch(term)
  }

  return (
    <div>
      <form onSubmit={handleSubmit}>
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
    </div>
  )
}
