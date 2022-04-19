import React from 'react';
import billSearch from "app/apis/billSearch";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import BillSearchResults from "app/views/bills/search/BillSearchResults";
import LoadingIndicator from "app/shared/LoadingIndicator";
import BillSearchForm from "app/views/bills/search/BillSearchForm";
import { initialRefineState, } from "app/views/bills/search/billSearchUtils";
import QuickSearchTips from "app/views/bills/search/QuickSearchTips";
import AdvancedSearchTips from "app/views/bills/search/AdvancedSearchTips";
import ErrorMessage from "app/shared/ErrorMessage";

export default function BillSearch() {
  const [ response, setResponse ] = React.useState([])
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const limit = 6
  const [ errorMsg, setErrorMsg ] = React.useState("")

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
      if (value && initialRefineState[key]) {
        const filterTerm = initialRefineState[key].searchTerm(value);
        if (filterTerm) {
          searchTerm += ` AND ${filterTerm}`
        }
      }
    })

    doSearch(searchTerm, session, limit, offset, sort)
  }

  const doSearch = (term, session, limit, offset, sort) => {
    setLoading(true)
    setErrorMsg("")
    billSearch(term, session, limit, offset, sort)
      .then((response) => {
        setResponse(response)
      })
      .catch((error) => {
        setResponse([])
        setErrorMsg(error.message)
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
      {loading &&
        <LoadingIndicator />
      }
      {!loading && errorMsg &&
        <div className="text-center">
          <ErrorMessage>{errorMsg}</ErrorMessage>
        </div>
      }
      {!loading && !errorMsg &&
        <BillSearchResults response={response}
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

