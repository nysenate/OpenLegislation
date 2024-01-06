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
import transcriptApi from "app/apis/transcriptApi";
import * as queryString from "query-string";
import Select, { yearSortOptions } from "app/shared/Select";
import Pagination from "app/shared/Pagination";
import Input from "app/shared/Input";
import LoadingIndicator from "app/shared/LoadingIndicator";
import HighlightedText from "app/shared/HighlightedText";
import {
  DATE_FULL,
  DATETIME_FULL_NO_ZONE,
  formatDateTime
} from "app/lib/dateUtils";
import { capitalizePhrase } from "app/lib/textUtils";
import TranscriptDisplay from "app/views/transcripts/TranscriptDisplay";
import ErrorMessage from "app/shared/ErrorMessage";


const hearingSort = "_score:desc,date:desc"
const sessionSort = "_score:desc,dateTime:desc"

/**
 * The top-level function for transcripts.
 */
export default function Transcripts({ setHeaderText }) {
  return (
    <ContentContainer>
      <Switch>
        <Route exact path="/transcripts/session">
          <TranscriptListing isHearing={false} setHeaderText={setHeaderText} />
        </Route>
        <Route exact path="/transcripts/hearing">
          <HearingListing setHeaderText={setHeaderText} />
        </Route>
        <Route path="/transcripts/session/:dateTime/:sessionType">
          <Transcript isHearing={false} setHeaderText={setHeaderText} />
        </Route>
        <Route path="/transcripts/hearing/:id">
          <Transcript isHearing={true} setHeaderText={setHeaderText} />
        </Route>
      </Switch>
    </ContentContainer>
  )
}

/**
 * Needed to ensure page reloads when switching between transcript types.
 */
function HearingListing({ setHeaderText }) {
  return <TranscriptListing isHearing={true} setHeaderText={setHeaderText} />
}

/**
 * Gets the JSX for a single transcript.
 * Needed because useRouteMatch() doesn't work if called in default function.
 */
function Transcript({ isHearing, setHeaderText }) {
  return <TranscriptDisplay isHearing={isHearing} params={useRouteMatch().params} setHeaderText={setHeaderText} />
}

/**
 * Makes an API call to get a list of Transcripts, and passes the results to ResultList.
 */
function TranscriptListing({ isHearing, setHeaderText }) {
  const location = useLocation()
  let params = queryString.parse(location.search)
  const [ dirtyTerm, setDirtyTerm ] = React.useState(params.term ?? "")
  params = { year: params.year ?? "", page: params.page ?? "1", term: params.term ?? "" }
  const history = useHistory()

  const [ loading, setLoading ] = React.useState(true)
  const [ data, setData ] = React.useState({ result: { items: [] } })
  const [ errorMsg, setErrorMsg ] = React.useState("")

  React.useEffect(() => {
    setHeaderText(isHearing ? "Search Hearing Transcripts" : "Search Session Transcripts")
  }, [ isHearing ])

  React.useEffect(() => {
    setLoading(true)
    setErrorMsg("")
    transcriptApi(isHearing, params.year, params.page, params.term || "*", isHearing ? hearingSort : sessionSort)
      .then((data) => setData(data))
      .catch((error) => setErrorMsg(error.message))
      .finally(() => setLoading(false))
  }, [ isHearing, params.year, params.page, params.term ]);

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({ search: queryString.stringify(params) })
  }

  const onYearChange = event => {
    params.page = 1
    params.year = event.target.value
    history.push({ search: queryString.stringify(params) })
  }

  const onSubmit = e => {
    e.preventDefault()
    params.term = dirtyTerm
    history.push({ search: queryString.stringify(params) })
  }

  // Ensures part of the page is replaced by a loading bar if data is not available yet.
  const bottom = loading ? <LoadingIndicator /> :
    <div>
      <Pagination currentPage={params.page} limit={data.limit}
                  total={data.total} onPageChange={onPageChange} />
      <ResultList items={data.result.items} isHearing={isHearing} />
      <Pagination currentPage={params.page} limit={data.limit}
                  total={data.total} onPageChange={onPageChange} />
    </div>;

  return (
    <div className="p-3">
      <form onSubmit={onSubmit}>
        <div className="flex flex-wrap">
          <div className="flex-grow mr-8">
            <Input label="Search for Transcripts"
                   value={dirtyTerm}
                   onChange={(e) => setDirtyTerm(e.target.value)}
                   placeholder={"e.g. \"a phrase\" or keywords"}
                   name="transcriptSearch"
                   className="w-full"
                   tabIndex="1" />
          </div>
        </div>
        <div className="flex justify-end">
          <button className="btn btn--primary my-3 w-36" type="submit">Search</button>
        </div>
      </form>
      <div className="my-3">
        <Select label="Year" value={params.year} options={yearSortOptions(isHearing ? 2011 : 1993,
          true, false)}
                onChange={onYearChange} name="year" />
      </div>
      {errorMsg &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }

      {bottom}
    </div>
  )
}

/**
 * Displays a list of results (items).
 */
function ResultList({ items, isHearing }) {
  if (items.length === 0) {
    return <div className="my-3">No transcripts found</div>
  }
  if (!isHearing) {
    return <SessionTranscriptResultList transcriptSearchResults={items} />
  } else {
    return <HearingTranscriptResultList hearingSearchResults={items} />
  }
}

function HearingTranscriptResultList({ hearingSearchResults }) {
  return (
    <ol>
      {hearingSearchResults.map((h) =>
        <li key={h.result.id}>
          <Link to={`/transcripts/hearing/${h.result.id}`} className="link">
            <div className="hover:bg-gray-200 rounded px-3 py-2">
              <div className="flex flex-wrap lg:flex-nowrap">
                <div className="flex-none w-60 mr-3">
                  <div>
                    {formatDateTime(h.result.date, DATE_FULL)}
                  </div>
                </div>
                <div className="text w-full">
                  {h.result.title}

                  {h.highlights.text &&
                    <div className="pt-1 pb-6">
                      <HighlightedText highlights={removeExcessNewLines(h.highlights.text)} />
                    </div>
                  }
                </div>
              </div>
            </div>
          </Link>
        </li>
      )}
    </ol>
  )
}

function SessionTranscriptResultList({ transcriptSearchResults }) {
  return (
    <ol>
      {transcriptSearchResults.map((t) =>
        <li key={t.result.dateTime + t.result.sessionType}>
          <Link to={`/transcripts/session/${t.result.dateTime}/${t.result.sessionType}`} className="link">
            <div className="hover:bg-gray-200 rounded px-3 py-2">
              {formatDateTime(t.result.dateTime, DATETIME_FULL_NO_ZONE)}&nbsp;
              <span className="text">- {capitalizeSessionType(t.result.sessionType)}</span>

              {t.highlights.text &&
                <div className=" pt-1 pb-6">
                  <HighlightedText highlights={removeExcessNewLines(t.highlights.text)} />
                </div>
              }
            </div>
          </Link>
        </li>
      )}
    </ol>
  )
}

function capitalizeSessionType(type) {
  const typeSplit = type.split(' ')
  const lastPart = typeSplit.pop()
  // Roman numerals should remain all capitalized.
  if (!new RegExp('^[IV]+$').test(lastPart)) {
    return capitalizePhrase(type);
  }
  return capitalizePhrase(typeSplit.join(' ')) + " " + lastPart;
}

/**
 * Removes excess new lines in highlighted text for session and hearing transcripts.
 * Hearing transcripts have "\r\n\r\n" for each new line.
 * Session transcripts have "\n\n" for each new line.
 * @param highlights
 * @returns {*}
 */
const removeExcessNewLines = (highlights) => {
  return highlights.map((text) => text.replace(/(\r\n\r\n|\n\n)/gm, "\n"))
}
