import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";

export default function AgendaSearchResults({ response, limit, page, onPageChange}) {

  console.log(response)

  if (response.total === 0) {
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

function ResultList({ results, detail }) {
  // console.log(results)
  return (
    <div>
      {results.map((r) =>
        <ResultItem result={r} detail={detail} key={r.result.agendaId.number +
        r.result.agendaId.year + r.result.weekOf + r.result.totalBillsConsidered}/>
      )}
    </div>
  )
}

function ResultItem({ result, detail }) {
  // console.log(result)
  result = result.result
  // let array = Object.values(activeLists.items)

  return (

    <div>
      <Link to={`/agendas/view/${result.agendaId.year}/${result.agendaId.number}`}>
        <div className="p-3 hover:bg-gray-200 flex flex-wrap">
          <div className="py-1 w-full">

            <div className="grid grid-flow-col grid-rows-3 grid-cols-1 gap-2">
              <div className="row-start-1 flex items-center text mr-5">
                Agenda {result.agendaId.number} {result.agendaId.year}
              </div>

              <div className="row-start-2">
                <div className="flex items-center text text--small">
                  <p><b>Chamber: {result.committeeId.chamber} Committee Name: {result.committeeId.name} </b></p>
                </div>
              </div>

              <div className="row-start-3">
                <div className="flex items-center text text--small">
                  <p><b>Week Of: {result.weekOf} </b></p>
                </div>
              </div>
            </div>

          </div>
        </div>
      </Link>
    </div>
  )
}