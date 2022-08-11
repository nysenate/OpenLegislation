import React from 'react'
import Pagination from "app/shared/Pagination";


export default function LawUpdatesSearchResults({ response, limit, page, onPageChange }) {

  if (!Array.isArray(response?.result?.items) || !response.result.items.length) {
    return (
      <div>
        No results found
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="pt-3">

        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.total}
        />
        <ResultList results={response.result.items} />
        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.total}
        />

      </div>
    </div>
  )
}

function ResultList({ results }) {
  return (
    <div>
      {results.map((r) =>
        <ResultItem result={r} key={r.processedDateTime} />
      )}
    </div>
  )
}

function ResultItem({ result }) {
  return (
    <div>
      <div className="p-3">
        <div>
          <b>{result.action} - {result.scope} {result.id.lawId} ({result.id.locationId})</b>
        </div>

        <div className="">
          <div className="text text--small">
            Published Date: {result.id.publishedDate}
          </div>
        </div>

        <div className="">
          <div className="text text--small">
            Processed Date: {result.processedDateTime}
          </div>
        </div>

        <div className="">
          <div className="text text--small">
            Source: {result.sourceId}
          </div>
        </div>
      </div>
    </div>
  )
}