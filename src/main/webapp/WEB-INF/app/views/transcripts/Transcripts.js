import React from 'react'
import {
  Link,
  Route,
  Switch,
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import ContentContainer from "app/shared/ContentContainer";
import SessionTranscript from "app/views/transcripts/SessionTranscript";
import HearingTranscript from "app/views/transcripts/HearingTranscript";
import getTranscript, { transcriptSearchApi } from "app/apis/transcriptApi";
import * as queryString from "query-string";
import Select, { yearSortOptions } from "app/shared/Select";
import Pagination from "app/shared/Pagination";

export default function Transcripts() {
  return (
    <ContentContainer>
      <Switch>
        <Route exact path = "/transcripts/session">
          <SessionSearchResults/>
        </Route>
        <Route exact path = "/transcripts/hearing">
          <HearingSearchResults/>
        </Route>
        <Route path = "/transcripts/session/:key">
          <Transcript isHearing = {false}/>
        </Route>
        <Route path = "/transcripts/hearing/:key">
          <Transcript isHearing = {true}/>
        </Route>
      </Switch>
    </ContentContainer>
  )
}

// TODO: trigger re-render another way?
// Without these functions, React won't trigger the useEffect hook.
function HearingSearchResults() {
  return <TranscriptSearchResults isHearing = {true}/>
}

function SessionSearchResults() {
  return <TranscriptSearchResults isHearing = {false}/>
}

function TranscriptSearchResults({isHearing}) {
  const location = useLocation()
  const params = queryString.parse(location.search)
  params.page = params.page || "1"
  params.year = params.year || ""

  const [loading, setLoading] = React.useState(true)
  const [data, setData] = React.useState({result: {items: []}})
  React.useEffect(() => {transcriptSearchApi(isHearing, params.year, params.page)
    .then((data) => setData(data))
    .finally(() => setLoading(false))}, [params.year, params.page]);

  if (loading)
    return (<div>Loading ...</div>);
  const history = useHistory()
  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({search: queryString.stringify(params)})
  }
  const onYearChange = event => {
    onPageChange({selectedPage: 1})
    params.year = event.target.value
    history.push({search: queryString.stringify(params)})
  }
  return (<div className="pt-3">
    <Select label = {"Year"} value = {params.year} options = {yearSortOptions(isHearing ? 2011 : 1993)}
            onChange = {onYearChange} name = {"year"}/>
    <Pagination currentPage = {params.page} limit = {data.limit} total = {data.total} onPageChange = {onPageChange}/>
    <ResultList transcripts = {data.result.items} pathname = {location.pathname} isHearing = {isHearing}/>
  </div>)
}

function ResultList({transcripts, pathname, isHearing}) {
  if (transcripts.length === 0)
    return <div>No transcripts found!</div>
  const identifier = isHearing ? "id" : "dateTime"
  return (
    <div>{transcripts.map((transcript) =>
        <div className = "col mt-1 text text-blue-600">
          <Link to = {pathname + "/" + transcript[identifier]}>
            {isHearing ? transcript.date : transcript.dateTime}
          </Link>
          {isHearing ? " - " + transcript.title : ""}<br/>
        </div>
    )}
    </div>)
}

function Transcript({isHearing}) {
  const id = useRouteMatch().params.key;
  const [loading, setLoading] = React.useState(true)
  const [transcript, setTranscript] = React.useState([]);
  React.useEffect(() => {getTranscript(isHearing, id)
      .then((res) => setTranscript(res.result))
      .finally(() => setLoading(false))},
    [isHearing, id]);
  if (loading)
    return (<div>Loading ...</div>);
  else if (isHearing)
    return <HearingTranscript hearing = {transcript}/>
  else
    return <SessionTranscript session = {transcript}/>
}
