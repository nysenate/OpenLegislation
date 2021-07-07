import React, { useEffect } from 'react';
import billSearch from "app/apis/billSearch";
import { Link } from "react-router-dom";

export default function Search() {
  const [ results, setResults ] = React.useState([]);

  return (
    <>
      <BillSearch setResults={setResults} />
      <Results results={results} />
    </>
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
    <div className="p-3">
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="billsearch">
            Search for legislation by print number or term:
          </label>
        </div>
        <div className="flex items-baseline">
          <input ref={termRef}
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
  const resultEls = results.length > 0
    ? results.map((r) =>
      <li key={r.result.basePrintNoStr} className='m-2'>
        <Link to={`/bills/${r.result.session}/${r.result.printNo}`}>
          {r.result.basePrintNoStr}
        </Link>
      </li>)
    : [];

  return (
    <div>
      {resultEls}
    </div>
  )
}
