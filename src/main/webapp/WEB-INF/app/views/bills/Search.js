import React from 'react';
import billSearch from "app/apis/billSearch";
import {
  Link,
  useLocation,
  useHistory
} from "react-router-dom";
import MemberThumbnail from "app/shared/MemberThumbnail";
import FullDate from "app/shared/FullDate";
import BillStatusDesc from "app/shared/BillStatusDesc";
import BillMilestones from "app/shared/BillMilestones";
import * as queryString from "query-string";

export default function Search() {
  // TODO implement pagination
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const limit = 6

  // When the url changes, perform a new search using the term from the query string.
  React.useEffect(() => {
    const term = params.term || '*'
    const page = params.page || 1
    const offset = (page - 1) * limit + 1
    doSearch(term, limit, offset)
  }, [ location ])

  // Updates the queryString with the new search term.
  const submitSearch = term => {
    params.term = term
    params.page = 1
    history.push({ search: queryString.stringify(params) })
  }

  const doSearch = (term, limit, offset) => {
    setLoading(true)
    billSearch(term, limit, offset)
      .then((response) => {
        setResponse(response)
        console.log(response)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`)
      })
      .finally(() => {
        setLoading(false)
      })
  }

  const onPageChange = page => {
    params.page = page
    history.push({ search: queryString.stringify(params) })
  }

  const pageCount = () => {
    return Math.ceil(response.total / limit)
  }

  return (
    <div className="p-3">
      <BillSearch submitSearch={submitSearch} />
      {!loading &&
      <>
        <Results results={response.result.items} />
      </>
      }
    </div>
  )
}

function BillSearch({ submitSearch }) {
  const termRef = React.useRef();

  const handleSubmit = (e) => {
    e.preventDefault();
    submitSearch(termRef.current.value)
  }

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="billsearch">
            Search for legislation by print number or term:
          </label>
        </div>
        <div className="flex items-baseline">
          <input ref={termRef}
                 tabIndex="1"
                 name="billsearch"
                 type="text"
                 className="input ml-2 mr-8 flex-grow"
                 placeholder="e.g. S1234-2015 or yogurt" />
          <button className="btn my-3 w-28 md:w-36" type="submit">Search</button>
        </div>
      </form>
    </div>
  )
}

function Results({ results }) {
  if (results.length === 0) {
    return (
      <div>
        No results found
      </div>
    )
  }

  return (
    <div>
      {results.map((r) =>
        <Link to={`/bills/${r.result.session}/${r.result.basePrintNo}`}
              key={r.result.basePrintNoStr}>
          <SearchResult result={r} />
        </Link>
      )}
    </div>
  )
}

function SearchResult({ result }) {
  const bill = result.result
  return (
    <div className="p-3 hover:bg-gray-200 flex flex-wrap">
      <div className="flex items-center w-full md:w-1/3">
        <MemberThumbnail member={bill.sponsor.member} />
        <div>
          <div className="text">
            {bill.basePrintNo}-{bill.session}
          </div>
          <div className="text text--small">
            {bill.sponsor && bill.sponsor.member &&
            bill.sponsor.member.fullName}
          </div>
        </div>
      </div>

      <div className="w-full md:w-2/3 mt-2 md:mt-0">
        <div className="text">
          {result.highlights.title
            ? <span className="highlight" dangerouslySetInnerHTML={{ __html: result.highlights.title }} />
            : <span>{bill.title}</span>
          }
        </div>
        {bill.status.actionDate &&
        <div className="mt-1 text text-blue-600">
          <FullDate date={bill.status.actionDate} /> - <BillStatusDesc status={bill.status} />
        </div>
        }
        <BillMilestones milestones={bill.milestones.items}
                        chamber={bill.billType.chamber}
                        className="py-3" />
      </div>
    </div>
  )
}
