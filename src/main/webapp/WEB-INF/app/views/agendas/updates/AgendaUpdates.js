import React from 'react';
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import { DateTime } from "luxon";
import { searchAgendaUpdates } from "app/apis/agendaApi";
import AgendaUpdatesForm from "app/views/agendas/updates/AgendaUpdatesForm";
import AgendaUpdatesResults from "app/views/agendas/updates/AgendaUpdatesResults";
import { PageParams } from "app/shared/Pagination";
import LoadingIndicator from "app/shared/LoadingIndicator";
import ErrorMessage from "app/shared/ErrorMessage";


export default function AgendaUpdates() {
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

  const loadSearchParams = search => {
    const params = queryString.parse(search)
    params.from ??= DateTime.now().minus({ months: 1 }).toISODate()
    params.to ??= DateTime.now().toISODate()
    params.type ??= "published" // Date type to filter on. One of published or processed.
    params.detail ??= "false"
    params.sort ??= "desc"
    params.page ??= 1
    params.limit ??= 6
    return params
  }

  React.useEffect(() => {
    if (searchParams.from && searchParams.to) {
      setIsLoading(true)
      setErrorMsg("")
      searchAgendaUpdates(searchParams)
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

  return (
    <div className="p-3">
      <AgendaUpdatesForm {...searchParams} />
      {isLoading &&
        <LoadingIndicator />
      }
      {!isLoading && errorMsg &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      {!isLoading &&
      <AgendaUpdatesResults response={response}
                            pageParams={new PageParams(searchParams.page, searchParams.limit)}
                            onPageChange={pageInfo => onPageChange(pageInfo)}
                            showDetail={searchParams.detail} />
      }
    </div>
  )
}
