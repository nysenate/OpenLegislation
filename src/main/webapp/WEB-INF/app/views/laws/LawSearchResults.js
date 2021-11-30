import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";

export default function LawSearchResults({ response, limit, page, onPageChange }) {

  if (response.result.items.length === 0) {
    return (
      <div>
        No results found
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="pt-3">
        <p className="center"> {response.total} matching law documents were found :)</p>
        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.size}
        />
        <ResultList results={response.result.items} />
        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.size}
        />
      </div>
    </div>
  )
}

function ResultList({ results }) {
  return (
    <div>
      {results.map((r) =>
        <Link to={`/laws/${r.result.lawId}?location=${r.result.locationId}`} key={r.rank}>
          <ResultItem result={r} key={r.rank} />
        </Link>
      )}
    </div>
  )
}

function ResultItem({ result }) {
  let highlights = result.highlights.text.toString().split('\\n').join(' ');
  return (
    <div>
      <div className="p-3 hover:bg-gray-200 flex flex-wrap">
        <div className="py-3 w-full md:w-1/3">
          <div className="flex items-center">
            <div className="text text--small">
              <p>{result.result.lawName} {result.result.docType} {result.result.docLevelId} {result.result.title}</p>
            </div>
            <div className="text text--small">
              <span className="highlight" dangerouslySetInnerHTML={{ __html: highlights }} />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}