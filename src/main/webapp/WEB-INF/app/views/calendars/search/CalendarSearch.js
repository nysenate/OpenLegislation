import React from 'react';
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import CalendarSearchForm from "app/views/calendars/search/CalendarSearchForm";
import { DateTime } from "luxon";
import { searchCalendars } from "app/apis/calendarApi";
import CalendarSearchResults from "app/views/calendars/search/CalendarSearchResults";
import ErrorMessage from "app/shared/ErrorMessage";


export default function CalendarSearch() {
  const [ searchParams, setSearchParams ] = React.useState({})
  const [ response, setResponse ] = React.useState({})
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const { search } = useLocation()
  const history = useHistory()

  /** Initialize search params if they are not set */
  React.useEffect(() => {
    const params = loadSearchParams(search)
    history.push({ search: queryString.stringify(params) })
  }, [])

  /** Update searchParams whenever a URL search parameter is updated. */
  React.useEffect(() => {
    const params = loadSearchParams(search)
    setSearchParams(params)
  }, [ search ])

  /** Perform a search whenever a searchParam field is changed. */
  React.useEffect(() => {
    if (searchParams.year) {
      setIsLoading(true)
      setErrorMsg("")
      searchCalendars(searchParams)
        .then(res => setResponse(res))
        .catch(err => {
          setResponse({})
          setErrorMsg(err.message)
        })
        .finally(() => setIsLoading(false))
    }
  }, [ searchParams ])

  const onPageChange = pageInfo => {
    const params = queryString.parse(search)
    params.page = pageInfo.selectedPage
    params.limit = pageInfo.limit
    history.push({ search: queryString.stringify(params) })
  }

  const loadSearchParams = search => {
    const params = queryString.parse(search)
    params.year ??= DateTime.now().year
    params.calendarNo ??= ""
    params.printNo ??= ""
    params.billCalendarNo ??= ""
    params.sort ??= "desc"
    params.page ??= 1
    params.limit ??= 8
    return params
  }

  return (
    <div className="p-3">
      <CalendarSearchForm {...searchParams} />
      {isLoading &&
        <LoadingIndicator />
      }
      {errorMsg && !isLoading &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      {!isLoading &&
        <CalendarSearchResults response={response}
                               onPageChange={onPageChange}
                               limit={searchParams.limit}
                               page={searchParams.page}
        />
      }
    </div>
  )
}
