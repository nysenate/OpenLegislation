import React from 'react';
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import AgendaSearchForm from "app/views/agendas/search/AgendaSearchForm";
import AgendaSearchResults from "app/views/agendas/search/AgendaSearchResults";
import { DateTime } from "luxon";
import { PageParams } from "app/shared/Pagination";
import { searchAgendas } from "app/apis/agendaApi";
import LoadingIndicator from "app/shared/LoadingIndicator";
import ErrorMessage from "app/shared/ErrorMessage";


export default function AgendaSearch() {
  const [ searchParams, setSearchParams ] = React.useState({})
  const [ searchResponse, setSearchResponse ] = React.useState({})
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const { search } = useLocation()
  const history = useHistory()

  /** Initialize search params if they are not set. */
  React.useEffect(() => {
    const params = loadSearchParams(search)
    history.push({ search: queryString.stringify(params) })
  }, [])

  const loadSearchParams = search => {
    const params = queryString.parse(search)
    params.from ??= DateTime.now().startOf('year').toISODate()
    params.to ??= DateTime.now().toISODate()
    params.committee ??= "any"
    params.sort ??= "desc"
    params.page ??= 1
    params.limit ??= 8
    params.printNo ??= ""
    params.notes ??= ""
    return params
  }

  /** Update searchParams whenever a url search parameter is updated. */
  React.useEffect(() => {
    const params = loadSearchParams(search)
    params.from = DateTime.fromISO(params.from)
    params.to = DateTime.fromISO(params.to)
    setSearchParams(params)
  }, [ search ])

  /** Perform a search whenever a search param is changed */
  React.useEffect(() => {
    if (searchParams.from && searchParams.to) {
      setIsLoading(true)
      setErrorMsg("")
      searchAgendas(searchParams)
        .then(res => setSearchResponse(res))
        .catch(err => {
          setSearchResponse({})
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
      <AgendaSearchForm {...searchParams} />
      {isLoading &&
        <LoadingIndicator />
      }
      {errorMsg && !isLoading &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      {!isLoading &&
        <AgendaSearchResults response={searchResponse}
                             pageParams={new PageParams(searchParams.page, searchParams.limit)}
                             onPageChange={pageInfo => onPageChange(pageInfo)} />
      }
    </div>
  )
}
