import React from 'react';
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import LawUpdatesSearchForm from "app/views/laws/LawUpdatesSearchForm";
import LawUpdatesSearchResults from "app/views/laws/LawUpdatesSearchResults";
import getLawUpdatesApi from "app/apis/getLawUpdatesApi";

export default function LawSearch() {
  const [ loading, setLoading ] = React.useState(true)
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const page = params.page || 1

  let aMonthAgo = new Date();
  aMonthAgo.setMonth(aMonthAgo.getMonth() - 1);

  const [ from, setFrom ] = React.useState(aMonthAgo.toISOString().slice(0, 10))
  const [ to, setTo ] = React.useState(new Date().toISOString().slice(0, 10))

  const [ withSelect, setWithSelect ] = React.useState('published')
  const [ sortSelect, setSortSelect ] = React.useState('desc')
  const [ limit, setLimit ] = React.useState(6)
  const [ offset, setOffset ] = React.useState(1)


  React.useEffect(() => {
    doSearch()
  }, [ location, from, to, withSelect, sortSelect ])

  const setSearchValues = (fromForm, toForm, withSelectForm, sortSelect) => {
    setFrom(fromForm)
    setTo(toForm)
    setWithSelect(withSelectForm)
    setSortSelect(sortSelect)
  }

  const doSearch = () => {
    setLoading(true)
    getLawUpdatesApi(true, from + 'T00:00:00.000', to + 'T00:00:00.000', withSelect, sortSelect, limit, offset)
      .then((response) => {
        console.log(response)
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
    setOffset(pageInfo.offset)
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="p-3">
      <LawUpdatesSearchForm updateValues={setSearchValues} aMonthAgo={from} todaysDate={to} />
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

