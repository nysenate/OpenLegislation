import React from 'react'
import BillUpdatesForm from "app/views/bills/updates/BillUpdatesForm";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import {
  useHistory,
  useLocation,
} from "react-router-dom";
import { fetchAllBillUpdates } from "app/apis/billGetApi";
import * as queryString from "query-string";
import Pagination from "app/shared/Pagination";
import BillUpdateResults from "app/views/bills/updates/BillUpdateResults";
import ErrorMessage from "app/shared/ErrorMessage";

/**
 * Valid search parameters for this page.
 * This constructor copies the "params" parameter and adds default values for any missing search parameters.
 *
 * @param params {Object} search params
 * @param params.from {string} An ISO date time string. i.e. 2021-06-02T00:00:00.000
 * @param params.to {string} An ISO date time string. i.e. 2021-10-21T23:59:59.999
 * @param params.type {string} The type of date to filter by. "published" or "processed"
 * @param params.order {string} The sort order for results. "asc" or "desc"
 * @param params.filter {string} The "Content Type" filter, limits the search to certain updated fields.
 *                               An empty string searches all fields.
 * @param params.detail {boolean} Include update details in the search results if true.
 * @param params.page {number} The current active page of results.
 * @param params.limit {number} The current number of results per page.
 * @param params.offset {number} The offset needed to query the current page.
 * @constructor
 */
function SearchParams(params) {
  return {
    from: params.from || DateTime.local().minus({ days: 5 }).toISO({ includeOffset: false }),
    to: params.to || DateTime.local().toISO({ includeOffset: false }),
    type: params.type || "published",
    order: params.order || "desc",
    filter: params.filter || "",
    detail: params.detail || false,
    page: params.page || 1,
    limit: params.limit || 20,
    offset: params.offset || 1,
  }
}

/**
 * The Bill Updates search component
 *
 * This component stores its state, i.e. form data and pagination data, in the URL. It re-renders and updates
 * the search results whenever the url changes.
 */
export default function BillUpdates() {
  const location = useLocation()
  const history = useHistory()
  const [ response, setResponse ] = React.useState()
  const params = new SearchParams(queryString.parse(location.search, { parseBooleans: true }))
  const [ errorMsg, setErrorMsg ] = React.useState("")

  React.useEffect(() => {
    setResponse(undefined)
    setErrorMsg("")
    fetchAllBillUpdates(params)
      .then((response) => {
        setResponse(response)
      })
      .catch((error) => {
        setResponse([])
        setErrorMsg(error.message)
      })
  }, [ location.search ])

  /**
   * Called when the search form is submitted.
   * This triggers a new search and resets pagination search params.
   * @param formParams {SearchParams} SearchParams entered in the form.
   */
  const doSearch = formParams => {
    history.push({ search: queryString.stringify(new SearchParams(formParams)) })
  }

  const onPageChange = pageLimOff => {
    params.page = pageLimOff.selectedPage
    params.limit = pageLimOff.limit
    params.offset = pageLimOff.offset
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="p-3">
      <div className="my-2">
        <BillUpdatesForm doSearch={doSearch} formData={params} />
      </div>
      {!response &&
        <div className="my-2">
          <LoadingIndicator />
        </div>
      }
      {errorMsg &&
        <div className="text-center my-8">
          <ErrorMessage>{errorMsg}</ErrorMessage>
        </div>
      }
      {!errorMsg &&
        <div className="mt-8 text-center">
          <ResultSummary response={response} />
        </div>
      }
      <React.Fragment>
        <div className="my-3 mt-5">
          <Pagination
            limit={params.limit || 20}
            currentPage={params.page || 1}
            onPageChange={onPageChange}
            total={response?.total} />
        </div>
        <div className="my-3">
          <BillUpdateResults billUpdates={response?.result?.items} showDetail={params.detail} />
        </div>
        <div className="my-3">
          <Pagination
            limit={params.limit || 20}
            currentPage={params.page || 1}
            onPageChange={onPageChange}
            total={response?.total} />
        </div>
      </React.Fragment>
    </div>
  )
}

function ResultSummary({ response }) {
  if (!response) {
    return null
  }

  return (
    <React.Fragment>
      <span className="font-semibold">{response.total}</span> matches found between&nbsp;
      <span className="block md:inline">
        {formatDateTime(response.fromDateTime, DateTime.DATE_MED)} and {formatDateTime(response.toDateTime, DateTime.DATE_MED)}
      </span>
    </React.Fragment>
  )
}
