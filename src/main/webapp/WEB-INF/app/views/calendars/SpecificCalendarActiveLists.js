import React from 'react'
import {
  Link,
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import * as queryString from "query-string";
import BillListing from "app/shared/BillListing";

export default function SpecificCalendarActiveLists({ response }) {

  console.log(response)

  if (response.success === false) {
    return (
      <div>
        No results found
      </div>
    )
  }

  const activeLists = response.activeLists

  return (
    <div className="mt-8">
      <div className="pt-3">
        <ResultList results={activeLists} />
      </div>
    </div>
  )
}

function ResultList({ results }) {
  const list = results.items.entries
//collapsable
  return (
    <div>
      {list.map((r) =>
        <BillListing bill={r.result}
                     highlights={r.highlights.title}
                     to={`/bills/${r.result.session}/${r.result.basePrintNo}`}
                     key={r.basePrintNoStr} />
      )}
    </div>
  )
}


