import React, { useEffect } from 'react';
import billSearch from "app/apis/billSearch";
import { Link } from "react-router-dom";
import MemberThumbnail from "app/shared/MemberThumbnail";
import FullDate from "app/shared/FullDate";
import BillStatusDesc from "app/shared/BillStatusDesc";

export default function Search() {
  const [ results, setResults ] = React.useState([]);

  return (
    <div className="p-3">
      <BillSearch setResults={setResults} />
      <Results results={results} />
    </div>
  )
}

function BillSearch({ setResults }) {
  const termRef = React.useRef();

  useEffect(() => {
    search("*")
  }, [])

  const handleSubmit = (e) => {
    e.preventDefault();

    search(termRef.current.value)
  }

  const search = term => {
    billSearch(term)
      .then((response) => {
        setResults(response.result.items);
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`);
      })
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
          <BillSummary bill={r.result} />
        </Link>
      )}
    </div>
  )
}

function BillSummary({ bill }) {
  return (
    <div className="p-3 hover:bg-gray-200 flex flex-wrap">
      <div className="flex items-center w-full md:w-1/3">
        <MemberThumbnail member={bill.sponsor.member} />
        <div>
          <div className="text">
            {bill.basePrintNo}-{bill.session}
          </div>
          <div className="text text--small">
            {bill.sponsor.member.fullName}
          </div>
        </div>
      </div>
      <div className="w-full md:w-2/3 mt-2 md:mt-0">
        <div className="text">
          {bill.title}
        </div>
        {bill.status.actionDate &&
        <div className="mt-1 text text-blue-600">
          <FullDate date={bill.status.actionDate} /> - <BillStatusDesc status={bill.status} />
        </div>
        }
      </div>
    </div>
  )
}
