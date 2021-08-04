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
  actionTextTerm,
  agendaNoTerm,
  billTypeSearchTerm,
  calendarNoTerm,
  chamberSearchTerm,
  committeeTerm,
  fullTextTerm,
  lawCodeTerm,
  lawSectionTerm,
  memoTerm,
  printNoTerm,
  sponsorSearchTerm,
  statusTypeTerm,
  titleTerm
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
    const printNo = params.printNo
    const memo = params.memo
    const actionText = params.actionText
    const calendarNo = params.calendarNo
    const lawSection = params.lawSection
    const title = params.title
    const fullText = params.fullText
    const committee = params.committee
    const agendaNo = params.agendaNo
    const lawCode = params.lawCode

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
    if (printNo) {
      searchTerm += ` AND ${printNoTerm(printNo)}`
    }
    if (memo) {
      searchTerm += ` AND ${memoTerm(memo)}`
    }
    if (actionText) {
      searchTerm += ` AND ${actionTextTerm(actionText)}`
    }
    if (calendarNo) {
      searchTerm += ` AND ${calendarNoTerm(calendarNo)}`
    }
    if (lawSection) {
      searchTerm += ` AND ${lawSectionTerm(lawSection)}`
    }
    if (title) {
      searchTerm += ` AND ${titleTerm(title)}`
    }
    if (fullText) {
      searchTerm += ` AND ${fullTextTerm(fullText)}`
    }
    if (committee) {
      searchTerm += ` AND ${committeeTerm(committee)}`
    }
    if (agendaNo) {
      searchTerm += ` AND ${agendaNoTerm(agendaNo)}`
    }
    if (lawCode) {
      searchTerm += ` AND ${lawCodeTerm(lawCode)}`
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

