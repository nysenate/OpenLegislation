import React from 'react'
import Pagination from "app/shared/Pagination";

export default function LawUpdatesSearchResults({ response, limit, page, onPageChange }) {

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
      <div className="p-3 hover:bg-gray-200 flex flex-wrap">
        <div className="py-3 w-full">

          <div className="grid grid-flow-col grid-rows-4 grid-cols-1 gap-4">

            <div className="row-start-1 flex items-center text mr-5">
              <b>{result.action} - {result.scope} {result.id.lawId} ({result.id.locationId})</b>
            </div>

            <div className="row-start-2">
              <div className="flex items-center text text--small">
                <p><b>Published Date - {result.id.publishedDate} </b></p>
              </div>
            </div>

            <div className="row-start-3">
              <div className="flex items-center text text--small">
                <p><b>Processed Date - {result.processedDateTime} </b></p>
              </div>
            </div>

            <div className="row-start-4">
              <div className="flex items-center text text--small">
                <p><b>Source - {result.sourceId} </b></p>
              </div>
            </div>

          </div>

        </div>
      </div>
    </div>
  )
}