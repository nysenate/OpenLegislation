import React from 'react'
import { transcriptSearchApi } from "app/apis/transcriptApi";
import Pagination from "app/shared/Pagination";
import * as queryString from "query-string";
import {
  Link,
  useHistory,
  useLocation
} from "react-router-dom";
import Select, { yearSortOptions } from "app/shared/Select";

export default function SessionSearchResults() {
  const location = useLocation()
  const params = queryString.parse(location.search)
  params.page = params.page || "1"
  params.year = params.year || ""

  const history = useHistory()
  const [loading, setLoading] = React.useState(true)
  const [data, setData] = React.useState({result: {items: []}})

  React.useEffect(() => {transcriptSearchApi(false, params.year, params.page)
    .then((data) => {setData(data); setLoading(false)})}, [params.year, params.page]);
  if (loading)
    return (<div>Loading ...</div>);
  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({search: queryString.stringify(params)})
  }
  const onYearChange = event => {
    onPageChange({selectedPage: 1})
    params.year = event.target.value
    history.push({search: queryString.stringify(params)})
  }
  // TODO: don't hard-code year?
  // TODO: how should it display years without data?
  return (<div className="pt-3">
    <Select label = {"Session Year"} value = {params.year} options = {yearSortOptions(1993)} onChange = {onYearChange} name = {"year"}/>
    <Pagination currentPage = {params.page} limit = {data.limit} total = {data.total} onPageChange = {onPageChange}/>
    <ResultList transcripts = {data.result.items} pathname = {location.pathname}/>
  </div>)
}

function ResultList({transcripts, pathname}) {
  return (
    <div>{transcripts.map((transcript) =>
      <Link to = {pathname + "/" + transcript.dateTime} key = {transcript.dateTime}>
        <div className = "col mt-1 text text-blue-600">
          {transcript.dateTime}
        </div>
        <br/>
      </Link>)}
    </div>)
}
