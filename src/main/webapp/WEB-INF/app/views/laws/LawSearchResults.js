import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";
import HighlightedText from "app/shared/HighlightedText";

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
        <p className="center"> {response.total} matching law documents were found.</p>
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
      <hr />
    </div>
  )
}

function ResultList({ results }) {
  return (
    <div>
      {results.map((r) =>
        <ResultItem result={r} key={r.rank} />
      )}
    </div>
  )
}

function ResultItem({ result }) {
  const highlights = result.highlights.text.map((text) => text.replace(/\\n/gm, "\n"))
  return (
    <div className="my-4">
      <Link to={`/laws/${result.result.lawId}?location=${result.result.locationId}`}
            key={result.result.lawId + result.result.docLevelId}>
        <div className="p-3 flex flex-wrap hover:bg-gray-200 hover:pointer rounded">
          <div className="w-full lg:w-3/12 mr-5 mb-1">
            <div>
              <span className="h5">{result.result.lawId}</span>
              <span className="text font-extralight"> | </span>
              <span className="h5">{result.result.lawName}</span>
            </div>
            <div className="text text--small">{result.result.docType} {result.result.docLevelId}</div>
          </div>
          <div className="w-full lg:w-8/12">
            <HighlightedText highlights={highlights} />
          </div>
        </div>
      </Link>
    </div>
  )
}