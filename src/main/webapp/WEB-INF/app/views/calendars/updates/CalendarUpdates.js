import React from 'react';
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import { getCalendarUpdates } from "app/apis/calendarApi";
import { DateTime } from "luxon";
import CalendarUpdatesForm from "app/views/calendars/updates/CalendarUpdatesForm";
import LoadingIndicator from "app/shared/LoadingIndicator";
import ErrorMessage from "app/shared/ErrorMessage";
import CalendarUpdatesResults from "app/views/calendars/updates/CalendarUpdatesResults";


export default function CalendarUpdates() {
  const [ searchParams, setSearchParams ] = React.useState({})
  const [ response, setResponse ] = React.useState({})
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const { search } = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    const params = loadSearchParams(search)
    history.push({ search: queryString.stringify(params) })
  }, [])

  React.useEffect(() => {
    const params = loadSearchParams(search)
    params.from = DateTime.fromISO(params.from)
    params.to = DateTime.fromISO(params.to)
    params.detail = params.detail === "true"
    setSearchParams(params)
  }, [ search ])

  React.useEffect(() => {
    if (searchParams.from && searchParams.to) {
      setIsLoading(true)
      setErrorMsg("")
      getCalendarUpdates(searchParams)
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
    params.from ??= DateTime.now().minus({ months: 1 }).toISODate()
    params.to ??= DateTime.now().toISODate()
    params.type ??= "published"
    params.detail ??= "false"
    params.sort ??= "desc"
    params.page ??= 1
    params.limit ??= 6
    return params
  }

  return (
    <div className="p-3">
      <CalendarUpdatesForm {...searchParams} />
      {isLoading &&
        <LoadingIndicator />
      }
      {!isLoading && errorMsg &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      {!isLoading &&
        <CalendarUpdatesResults response={response}
                                page={searchParams.page}
                                limit={searchParams.limit}
                                onPageChange={pageInfo => onPageChange(pageInfo)}
                                showDetail={searchParams.detail} />
      }
    </div>
  )
}
