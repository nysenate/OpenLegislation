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
  billTypeSearchTerm,
  chamberSearchTerm,
  sponsorSearchTerm,
  statusTypeTerm
} from "app/views/bills/billSearchUtils";

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
    const params = queryString.parse(location.search)
    const page = params.page || 1
    const offset = (page - 1) * limit + 1
    const term = params.term || '*'
    const sort = params.sort
    const session = params.session
    const chamber = params.chamber
    const billType = params.billType
    const sponsor = params.sponsor
    const statusType = params.statusType

    // Some search fields need to be added to the lucene search term
    let searchTerm = term
    if (chamber) {
      searchTerm += ` AND ${chamberSearchTerm(chamber)}`
    }
    if (billType) {
      searchTerm += ` AND ${billTypeSearchTerm(billType)}`
    }
    if (sponsor) {
      searchTerm += ` AND ${sponsorSearchTerm(sponsor)}`
    }
    if (statusType) {
      searchTerm += ` AND ${statusTypeTerm(statusType)}`
    }

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
    </div>
  )
}

