import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";
import { SummaryItemSmall } from "app/views/agendas/AgendaView";

export default function AgendaSearchResults({ response, pageParams, onPageChange }) {
  if (!response || !response.result) {
    return null
  }

  if (response.total === 0) {
    return (
      <div className="text-center">
        No matches found.
      </div>
    )
  }

  return (
    <div className="mt-3">
      <div className="text-center">
        <span className="font-semibold">{response.total.toLocaleString()}</span>&nbsp;matches found.
      </div>
      <div className="">
        <Pagination
          limit={pageParams.limit}
          currentPage={pageParams.selectedPage}
          onPageChange={onPageChange}
          total={response.total} />
        <ResultList searchResults={response.result.items} />
        <Pagination
          limit={pageParams.limit}
          currentPage={pageParams.selectedPage}
          onPageChange={onPageChange}
          total={response.total} />
      </div>
    </div>
  )
}

function ResultList({ searchResults }) {
  return (
    <div className="my-3">
      {searchResults.map((r, index, row) =>
        <div key={index}>
          <ResultItem searchResult={r} />
          {index < (row.length - 1) &&
            <hr />
          }
        </div>
      )}
    </div>
  )
}

function ResultItem({ searchResult }) {
  return (
    <div className="my-3 flex justify-between items-center flex-wrap">
      <div className="w-6/12 lg:w-2/12 mb-1 lg:mb-0">
        <Link to={`${searchResult.result.agendaId.year}/${searchResult.result.agendaId.number}`}>
          <span className="link">Agenda {searchResult.result.agendaId.number} - {searchResult.result.agendaId.year}</span>
        </Link>
        <br />
        <span className="text text--small">Week of: {searchResult.result.weekOf}</span>
      </div>
      <div className="w-6/12 lg:w-4/12 mb-1 lg:mb-0">
        <Link to={`${searchResult.result.agendaId.year}/${searchResult.result.agendaId.number}/${searchResult.result.committeeId.name}`}>
          <span className="link">{searchResult.result.committeeId.name}</span>
        </Link>
      </div>
      <div className="flex-1">
        <SummaryItemSmall count={searchResult.result.totalAddendum} label="Addenda" />
      </div>
      <div className="flex-1">
        <SummaryItemSmall count={searchResult.result.totalBillsConsidered} label="Bills on Agenda" />
      </div>
      <div className="flex-1">
        <SummaryItemSmall count={searchResult.result.totalBillsVotedOn} label="Bills Voted On" />
      </div>
    </div>
  )
}