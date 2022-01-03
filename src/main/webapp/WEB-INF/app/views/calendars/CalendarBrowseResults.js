import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";

export default function CalendarBrowseResults({ response, limit, page, onPageChange }) {

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
        <ResultItem result={r} key={r.rank} />
      )}
    </div>
  )
}

function ResultItem({ result }) {
  return (
    <div>
      <div className="p-3 hover:bg-gray-200 flex flex-wrap">
        <div className="py-3 w-full md:w-1/3">
          <div className="flex items-center">
            <div className="text text--small">
              <p>{result.result.lawName} {result.result.docType} {result.result.docLevelId} {result.result.title}</p>
            </div>
            <div className="text text--small">
              <p>{result.highlights.text}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}