import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";
import HighlightedText from "app/shared/HighlightedText";
import LoadingIndicator from "app/shared/LoadingIndicator";

export default function LawSearchResults({ response, pageParams, onPageChange, isLoading }) {
  if (isLoading) {
    return (
      <LoadingIndicator />
    )
  }

  if (!isLoading && (!response.total || response.total === 0)) {
    return (
      <div>
        No results found.
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="pt-3">
        <p className="text-center"> {response.total} matching law documents were found.</p>
        <Pagination
          limit={pageParams.limit}
          currentPage={pageParams.selectedPage}
          onPageChange={onPageChange}
          total={response.total}
        />
        <ResultList results={response.result.items} />
        <Pagination
          limit={pageParams.limit}
          currentPage={pageParams.selectedPage}
          onPageChange={onPageChange}
          total={response.total}
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
        <ResultItem result={r} key={r.result.lawId + r.result.locationId} />
      )}
    </div>
  )
}

function ResultItem({ result }) {
  const highlights = result.highlights?.text?.map((text) => text.replace(/\\n/gm, "\n"))
  return (
    <div className="my-4">
      <Link to={resultLinkTo(result)}
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

const resultLinkTo = result => {
  switch (result.result.docType) {
    case "CHAPTER":
      return `/laws/${result.result.lawId}`
    case "SECTION":
      return `/laws/${result.result.lawId}/leaf/${result.result.locationId}`
    default:
      return `/laws/${result.result.lawId}/node/${result.result.locationId}`
  }
}