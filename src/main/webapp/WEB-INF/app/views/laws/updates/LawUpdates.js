import React from 'react';
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import LawUpdatesSearchForm from "app/views/laws/updates/LawUpdatesSearchForm";
import LawUpdatesSearchResults from "app/views/laws/updates/LawUpdatesSearchResults";
import getLawUpdatesApi from "app/apis/getLawUpdatesApi";
import { DateTime } from "luxon";
import ErrorMessage from "app/shared/ErrorMessage";


export default function LawSearch() {
  const [ loading, setLoading ] = React.useState(true)
  const [ response, setResponse ] = React.useState()
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const page = params.page || 1

  const [ from, setFrom ] = React.useState(() => DateTime.now().minus({ month: 1 }).startOf("day"))
  const [ to, setTo ] = React.useState(DateTime.now())
  const [ withSelect, setWithSelect ] = React.useState('published')
  const [ sortSelect, setSortSelect ] = React.useState('desc')
  const [ limit, setLimit ] = React.useState(6)
  const [ offset, setOffset ] = React.useState(1)
  const [ errorMsg, setErrorMsg ] = React.useState("")

  React.useEffect(() => {
    doSearch()
  }, [ location, from, to, withSelect, sortSelect ])

  const setSearchValues = (fromForm, toForm, withSelectForm, sortSelect) => {
    setFrom(fromForm)
    setTo(toForm)
    setWithSelect(withSelectForm)
    setSortSelect(sortSelect)
    // Reset to page 1 when starting a new search
    params.page = 1
    history.push({ search: queryString.stringify(params) })
  }

  const doSearch = () => {
    setLoading(true)
    setErrorMsg("")
    getLawUpdatesApi(true, from, to, withSelect, sortSelect, limit, offset)
      .then((response) => setResponse(response))
      .catch((error) => setErrorMsg(error.message))
      .finally(() => setLoading(false))
  }

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    setOffset(pageInfo.offset)
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="p-3">
      <LawUpdatesSearchForm updateValues={setSearchValues} from={from} to={to} />
      {errorMsg &&
      <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      {loading
        ? <LoadingIndicator />
        :
        <div>
          <LawUpdatesSearchResults response={response} limit={limit} page={page} onPageChange={onPageChange} />
        </div>
      }
    </div>
  )
}

