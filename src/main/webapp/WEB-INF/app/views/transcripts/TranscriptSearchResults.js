import React from 'react'
import { transcriptSearchApi } from "app/apis/transcriptApi";
import Pagination from "app/shared/Pagination";
import * as queryString from "query-string";
import {
  Link,
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";

export default function TranscriptSearchResults() {
  const year = useRouteMatch().params.year;
  // TODO: this default should stay in the Java code
  const limit = 25
  const location = useLocation()
  const params = queryString.parse(location.search)
  params.page = params.page || 1
  let offset = (params.page - 1) * limit + 1
  const history = useHistory()
  const [loading, setLoading] = React.useState(true)
  const [data, setData] = React.useState({result: {items: []}})
  React.useEffect(() => {transcriptSearchApi(false, year, limit, offset)
    .then((data) => {setData(data); setLoading(false)})}, [year, limit, offset]);
  if (loading)
    return (<div>Loading ...</div>);
  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({search: queryString.stringify(params)})
  }
  return (<div className="pt-3">
    <Pagination currentPage = {params.page || 1} limit = {limit} total = {data.total} onPageChange = {onPageChange}/>
    <ResultList transcripts = {data.result.items}/>
  </div>)
}

function ResultList({transcripts}) {
  return (
    <div>{transcripts.map((transcript) =>
      <Link to = {location.pathname + "/" + transcript.dateTime} key = {transcript.dateTime}>
        <div className="text text--small">
          {transcript.dateTime}
        </div>
        <br/>
      </Link>)}
    </div>)
}
