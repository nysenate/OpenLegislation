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
import TranscriptDisplay, { getDisplayDate } from "app/views/transcripts/TranscriptDisplay";
import transcriptApi from "app/apis/transcriptApi";
import * as queryString from "query-string";
import Select, { yearSortOptions } from "app/shared/Select";
import Pagination from "app/shared/Pagination";
import Input from "app/shared/Input";
import LoadingIndicator from "app/shared/LoadingIndicator";
import HighlightedText from "app/shared/HighlightedText";

/**
 * The top-level function for transcripts.
 */
export default function Transcripts() {
  return (
    <ContentContainer>
      <Switch>
        <Route exact path="/transcripts/session">
          <TranscriptListing isHearing={false} />
        </Route>
        <Route exact path="/transcripts/hearing">
          <HearingListing />
        </Route>
        <Route path="/transcripts/session/:id">
          <Transcript isHearing={false} />
        </Route>
        <Route path="/transcripts/hearing/:id">
          <Transcript isHearing={true} />
        </Route>
      </Switch>
    </ContentContainer>
  )
}

/**
 * Needed to ensure page reloads when switching between transcript types.
 */
function HearingListing() {
  return <TranscriptListing isHearing={true} />
}

/**
 * Gets the JSX for a single transcript.
 * Needed because useRouteMatch() doesn't work if called in default function.
 */
function Transcript({ isHearing }) {
  return <TranscriptDisplay isHearing={isHearing} id={useRouteMatch().params.id} />
}

/**
 * Makes an API call to get a list of Transcripts, and passes the results to ResultList.
 */
function TranscriptListing({ isHearing }) {
  const location = useLocation()
  let params = queryString.parse(location.search)
  params = { year: params.year ?? "", page: params.page ?? "1", term: params.term ?? "" }
  const history = useHistory()

  const [ loading, setLoading ] = React.useState(true)
  const [ data, setData ] = React.useState({ result: { items: [] } })

  React.useEffect(() => {
    setLoading(true)
    transcriptApi(isHearing, params.year, params.page, params.term)
      .then((data) => setData(data))
      .finally(() => setLoading(false))
  }, [ isHearing, params.year, params.page, params.term ]);

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({ search: queryString.stringify(params) })
  }

  const onYearChange = event => {
    onPageChange({ selectedPage: 1 })
    params.year = event.target.value
    history.push({ search: queryString.stringify(params) })
  }

  const onTermChange = term => {
    params.term = term
    history.push({ search: queryString.stringify(params) })
  }

  // Ensures part of the page is replaced by a loading bar if data is not available yet.
  const bottom = loading ? <LoadingIndicator /> :
    <div>
      <Pagination currentPage={params.page} limit={data.limit}
                  total={data.total} onPageChange={onPageChange} />
      <ResultList items={data.result.items} isHearing={isHearing} />
    </div>;

  return (
    <div className="pt-3">
      <SearchBox term={params.term} setSearchTerm={onTermChange} /><br />
      <Select label="Year" value={params.year} options={yearSortOptions(isHearing ? 2011 : 1993)}
              onChange={onYearChange} name="year" />
      {bottom}
    </div>
  )
}

/**
 * Displays a text book that can be used to search transcripts.
 * @param term To search for
 * @param setSearchTerm Used to change URL params.
 * @returns {JSX.Element}
 * @constructor
 */
function SearchBox({ term, setSearchTerm }) {
  const name = "transcriptSearch"

  const onSubmit = (e) => {
    e.preventDefault();
    setSearchTerm(document.getElementById(name).value)
  }

  return (
    <form onSubmit={onSubmit}>
      <div className="flex flex-wrap">
        <div className="flex-grow mr-8">
          <Input label="Search for Transcripts" value={term} onChange={(e) => setSearchTerm(e.target.value)}
                 placeholder={"e.g. \"a phrase\" or keywords"} name={name} className="w-full" />
        </div>
      </div>
    </form>
  )
}

/**
 * Displays a list of results (items).
 */
function ResultList({ items, isHearing }) {
  if (items.length === 0)
    return <div>No transcripts found!</div>

  const identifier = isHearing ? "id" : "dateTime"
  const getTranscriptFromItem = (item) => item.result ?? item

  return (
    <ol>
      {items.map((item) =>
        <li key={getTranscriptFromItem(item)[identifier]}>
          <div className="col mt-1 text text-blue-600">
            <Link to={useLocation().pathname + "/" + getTranscriptFromItem(item)[identifier]}>
              {getDisplayDate(getTranscriptFromItem(item), isHearing)}
            </Link>
            {isHearing ? " - " + getTranscriptFromItem(item).title : ""}<br />
          </div>
          <div className="text text--small">
            <HighlightedText highlights={item.highlights} />
          </div>
          <br />
        </li>
      )}
    </ol>
  )
}
