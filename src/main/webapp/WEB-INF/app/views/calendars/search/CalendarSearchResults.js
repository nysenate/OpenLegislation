import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";

export default function CalendarSearchResults({ response, limit, page, onPageChange }) {
  if (!response || !response.result) {
    return null
  }

  if (response.total === 0) {
    return (
      <div className="text-center">
        No results found
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="text-center">
        <span className="font-semibold">{response.total.toLocaleString()}</span>&nbsp;matches found.
      </div>
      <div>
        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.total} />
        <ResultList results={response.result.items} />
        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.total} />
      </div>
    </div>
  )
}

function ResultList({ results }) {
  return (
    <div>
      {results.map((r, i) =>
        <ResultItem result={r.result} key={i} />
      )}
    </div>
  )
}

function ResultItem({ result }) {
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

  if (supplementalCalendars.size > 0) {
    let array = Object.values(supplementalCalendars.items)
    for (const suppCalendar of array) {
      let itemEntries = suppCalendar.totalEntries
      totalFloorBills = totalFloorBills + itemEntries
    }
  }

  return (
    <div className="flex gap-x-6 my-3">
      <div>
        <Link to={`/calendars/${result.year}/${result.calendarNumber}`}>
          <span className="link">Senate Calendar {result.calendarNumber}</span>
        </Link>
        <div className="text text--small">
          {formatDateTime(result.floorCalendar.calDate, DateTime.DATE_MED)}
        </div>
      </div>
      <div className="text text--small font-medium">
        <div>
          {totalActiveListEntries} Active List Bills
        </div>
        <div>
          {totalFloorBills} Floor Bills
        </div>
      </div>
      <div className="text text--small font-medium">
        <div>
          {activeListSupplementlSize} Active List Supplementals
        </div>
        <div>
          {result.supplementalCalendars.size} Floor Supplementals
        </div>
      </div>

    </div>
  )
}
