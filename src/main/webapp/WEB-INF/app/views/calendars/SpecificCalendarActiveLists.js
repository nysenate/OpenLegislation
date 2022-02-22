import React from 'react'
import {
  Link,
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import * as queryString from "query-string";
import BillListing from "app/shared/BillListing";
import Accordion from "app/shared/Accordion";

export default function SpecificCalendarActiveLists({ response }) {

  // console.log(response)

  if (response.success === false) {
    return (
      <div>
        No results found
      </div>
    )
  }

  const activeLists = response.result.activeLists
  let listOfBills = activeLists.items[0].entries.items

  return (
    <div className="mt-8">
      <div className="pt-3">
        <Accordion title="ACTIVE LISTS" startOpen={true}>
          <ResultList results={listOfBills} />
        </Accordion>
      </div>
    </div>
  )
}

function ResultList({ results }) {
  return (
    <div>
      {results.map((r) =>
        <BillListing bill={r}
                     highlights={r.title}
                     to={`/bills/${r.session}/${r.basePrintNo}`}
                     key={r.basePrintNoStr} />
      )}
    </div>
  )
}


