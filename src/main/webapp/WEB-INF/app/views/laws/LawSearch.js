import React from 'react';
import lawSearchApi from "app/apis/lawSearchApi";
import getLawsApi from "app/apis/getLawsApi";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import LawSearchForm from "app/views/laws/LawSearchForm";
import LawVolumeSearchResults from "app/views/laws/LawVolumeSearchResults";
import LawSearchResults from "app/views/laws/LawSearchResults";

export default function LawSearch() {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ searchResponse, setSearchResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const [ filter, setFilter ] = React.useState("")
  const [ term, setTerm ] = React.useState('*')
  const [ searching, setSearching ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const limit = 6


  React.useEffect(() => {
    search()
    doInitialSearch()
  }, [ filter, term ])

  // Perform a search using the query string parameters.
  function search() {
    const params = queryString.parse(location.search)
    const page = params.page || 1
    const offset = (page - 1) * limit + 1
    const sort = params.sort
    let searchTerm = term
    doSearch(searchTerm, limit, offset, sort)
  }

  const doSearch = (term, limit, offset, sort) => {
    setLoading(true)
    if (term !== '*' && !isEmpty(term)) {
      setSearching(true)
      lawSearchApi(term, limit, offset, sort)
        .then((response) => {
          setSearchResponse(response)
        })
        .catch((error) => {
          // TODO properly handle errors
          console.warn(`${error}`)
        })
        .finally(() => {
          setLoading(false)
        })
    } else {
      setLoading(false)
      setSearching(false)
    }
  }

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({ search: queryString.stringify(params) })
  }

  const doInitialSearch = () => {
    setLoading(true)
    getLawsApi(null, null)
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

  return (
    <div className="p-3">
      <LawSearchForm searchTerm={params.term} handleVolumeSearchFilter={setFilter} handleSearchTerm={setTerm} />
      {loading
        ? <LoadingIndicator />
        :
        <div>
          {searching
            ?
            <LawSearchResults response={searchResponse} limit={limit} page={params.page} onPageChange={onPageChange} />
            : <div></div>
          }
          <LawVolumeSearchResults response={response} filter={filter} />
        </div>
      }
    </div>
  )

  function isEmpty(str) {
    return (!str || str.length === 0);
  }
}

