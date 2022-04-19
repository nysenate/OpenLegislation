import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";

export default function CalendarSearchResults({ response, limit, page, onPageChange, detail: activeListOnly }) {

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
        <ResultList results={response.result.items} detail={activeListOnly} key={generateKey} />
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

function generateKey() {
  return new Date().getTime()
}

function ResultList({ results, detail }) {
  console.log(results)
  return (
    <div>
      {results.map((r) =>
        <ResultItem result={r} detail={detail} />
      )}
    </div>
  )
}

function ResultItem({ result, detail }) {
  result = result.result
  // console.log(result)

  let floorCalendar = result.floorCalendar
  let supplementalCalendars = result.supplementalCalendars
  let activeLists = result.activeLists

  let activeListSupplementlSize = result.activeLists.size - 1;
  if (activeListSupplementlSize < 0) {
    activeListSupplementlSize = 0
  }

  let totalActiveListEntries = 0;
  if (activeLists.size > 0) {
    let array = Object.values(activeLists.items)
    for (const activeList of array) {
      let itemEntries = activeList.totalEntries
      totalActiveListEntries = totalActiveListEntries + itemEntries
    }
  }

  let totalFloorBills = 0;
  totalFloorBills = totalFloorBills + floorCalendar.totalEntries

  // console.log(supplementalCalendars)
  if (supplementalCalendars.size > 0) {
    let array = Object.values(supplementalCalendars.items)
    for (const suppCalendar of array) {
      let itemEntries = suppCalendar.totalEntries
      totalFloorBills = totalFloorBills + itemEntries
    }
  }

  return (

    <div>
      <Link to={`/calendars/view/${result.year}/${result.calendarNumber}`}>
      <div className="p-3 hover:bg-gray-200 flex flex-wrap">
        <div className="py-3 w-full">

          <div className="grid grid-flow-col grid-rows-5 grid-cols-1 gap-4">

            <div className="row-start-1 flex items-center text mr-5">
              <b>Calendar Number {result.calendarNumber} ({result.calDate})</b>
            </div>

            <div className="row-start-2">
              <div className="flex items-center text text--small">
                <p><b>{totalActiveListEntries} Active List Bills</b></p>
              </div>
            </div>

            <div className="row-start-3">
              <div className="flex items-center text text--small">
                <p><b>{activeListSupplementlSize} Active List Supplementals</b></p>
              </div>
            </div>

            <div className="row-start-4">
              <div className="flex items-center text text--small">
                <p><b>{totalFloorBills} Floor Bills</b></p>
              </div>
            </div>

            <div className="row-start-5">
              <div className="flex items-center text text--small">
                <p><b>{result.supplementalCalendars.size} Supplemental Floor Bills</b></p>
              </div>
            </div>

          </div>

        </div>
      </div>
      </Link>
    </div>
)
}