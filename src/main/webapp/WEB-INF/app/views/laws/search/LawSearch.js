import React from 'react';
import lawSearchApi from "app/apis/lawSearchApi";
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import LawSearchForm from "app/views/laws/search/LawSearchForm";
import LawChapterList from "app/views/laws/search/LawChapterList";
import LawSearchResults from "app/views/laws/search/LawSearchResults";
import { PageParams } from "app/shared/Pagination";
import ErrorMessage from "app/shared/ErrorMessage";


export default function LawSearch() {
  const { search } = useLocation()
  const history = useHistory()
  const [ searchParams, setSearchParams ] = React.useState({})
  const [ response, setResponse ] = React.useState({})
  const [ isLoading, setIsLoading ] = React.useState(false)
  const [ errorMsg, setErrorMsg ] = React.useState("")

  /** Initialize search params if they are not set */
  React.useEffect(() => {
    const params = queryString.parse(search)
    params.term ??= ""
    params.chapterFilter ??= ""
    params.page ??= 1
    params.limit ??= 6
    history.push({ search: queryString.stringify(params) })
  }, [])

  /** Whenever search param's are changed, updated the params on the state. */
  React.useEffect(() => {
    const params = queryString.parse(search)
    setSearchParams(params)
  }, [ search ])

  /** Whenever term or page are changed, perform a search. */
  React.useEffect(() => {
    if (searchParams.term) {
      setIsLoading(true)
      setErrorMsg("")
      lawSearchApi(searchParams.term, new PageParams(searchParams.page, searchParams.limit))
        .then((response) => setResponse(response))
        .catch((error) => {
          setResponse({})
          setErrorMsg(error.message)
        })
        .finally(() => setIsLoading(false))
    } else {
      setResponse({})
    }
  }, [ searchParams.term, searchParams.page ])

  const onPageChange = pageInfo => {
    const params = queryString.parse(search)
    params.page = pageInfo.selectedPage
    params.limit = pageInfo.limit
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="p-3">
      <LawSearchForm term={searchParams.term} filter={searchParams.chapterFilter} />
      {errorMsg &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      <div>
        {searchParams.term &&
          <LawSearchResults response={response}
                            pageParams={new PageParams(searchParams.page, searchParams.limit)}
                            onPageChange={onPageChange}
                            isLoading={isLoading} />
        }
        {!searchParams.term &&
          <LawChapterList filter={searchParams.chapterFilter} />
        }
      </div>
    </div>
  )
}
