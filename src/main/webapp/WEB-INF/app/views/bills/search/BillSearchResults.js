import React from 'react'
import Pagination from "app/shared/Pagination";
import BillListing from "app/shared/BillListing";

export default function BillSearchResults({ response, limit, page, onPageChange }) {

  if (!Array.isArray(response.result?.items) || !response.result.items.length) {
    return (
      <div className="flex justify-center mb-10">
        No results found
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="flex justify-center">
        <span className="font-semibold">{response.total.toLocaleString()}</span>&nbsp;matching bills were found.
      </div>
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
          <BillListing bill={r.result}
                       highlights={r.highlights.title}
                       to={`/bills/${r.result.session}/${r.result.basePrintNo}`}
                       key={r.result.basePrintNoStr} />
      )}
    </div>
  )
}
