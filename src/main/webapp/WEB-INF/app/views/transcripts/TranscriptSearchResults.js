import React from 'react'
import getTranscriptApi from "app/apis/getTranscriptApi";
import Pagination from "app/shared/Pagination";
import * as queryString from "query-string";
import {
  Link,
  useHistory,
  useLocation
} from "react-router-dom";

export default function TranscriptSearchResults() {
  const history = useHistory()
  const [loading, setLoading] = React.useState(true)
  const [data, setData] = React.useState({result: {items: []}})
  React.useEffect(() => {getTranscriptApi(false, null, null, false)
    .then((data) => {setData(data); setLoading(false)})});
  if (loading)
    return (<div>Loading ...</div>);
  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({search: queryString.stringify(params)})
  }
  return (<div className="pt-3">
    <Pagination limit = {25} currentPage = {1} total = {data.total} onPageChange = {onPageChange}/>
    <ResultList transcripts = {data.result.items}/>
  </div>)
}

function ResultList({transcripts}) {
  return (
    <div>{transcripts.map((transcript) =>
      <Link to = {useLocation().pathname + "/" + transcript.dateTime} key = {transcript.dateTime}>
        <div className="text text--small">
          {transcript.dateTime}
        </div>
      </Link>)}
    </div>)
}
