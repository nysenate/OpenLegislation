import React from 'react';
import billSearch from "app/apis/billSearch";
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import BillSearchResults from "app/views/bills/BillSearchResults";
import LoadingIndicator from "app/shared/LoadingIndicator";
import BillSearchForm from "app/views/bills/BillSearchForm";
import {
  REFINE,
} from "app/views/bills/billSearchUtils";
import QuickSearchTips from "app/views/bills/QuickSearchTips";
import AdvancedSearchTips from "app/views/bills/AdvancedSearchTips";

export default function BillSearch() {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const limit = 6

  /**
   * Whenever the query params are changed perform a new search.
   * Query params are changed by the BillSearchForm component whenever the search form is submitted.
   */
  React.useEffect(() => {
    search()
  }, [ location ])

  // Perform a search using the query string parameters.
  const search = () => {
    const params = queryString.parse(location.search, { parseBooleans: true })
    const page = params.page || 1
    const offset = (page - 1) * limit + 1
    const term = params.term || '*'
    const sort = params.sort
    const session = params.session

    let searchTerm = term
    Object.entries(params).forEach(([ key, value ]) => {
      if (value) {
        if (key in REFINE.PATHS) {
          searchTerm += ` AND ${REFINE.PATHS[key]}:(${value})`
        } else if (key in REFINE.FIXED_PATHS) {
          searchTerm += ` AND ${REFINE.FIXED_PATHS[key]}`
        }
      }
    })

    doSearch(searchTerm, session, limit, offset, sort)
  }

  const doSearch = (term, session, limit, offset, sort) => {
    setLoading(true)
    billSearch(term, session, limit, offset, sort)
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
      <BillSearchForm searchTerm={params.term} />
      {loading
        ? <LoadingIndicator />
        : <BillSearchResults response={response}
                             limit={limit}
                             page={params.page}
                             onPageChange={onPageChange} />
      }
      <div className="m-3">
        <QuickSearchTips />
      </div>
      <div className="m-3">
        <AdvancedSearchTips />
      </div>
    </div>
  )
}

