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

export default function SpecifiedCalendarFloor({ response }) {

  // console.log(response.result)

  if (response.success === false) {
    return (
      <div>
        No results found
      </div>
    )
  }

  const floor = response.result.floorCalendar
  const supplementals = response.result.supplementalCalendars

  let supplementalsExist = false
  if (supplementals.size > 0) {
    supplementalsExist = true
  }

  const entriesExist = sectionType => {
    for (let [ key, value ] of Object.entries(floor.entriesBySection.items)) {
      if (key === sectionType) {
        return true
      }
    }
  }

  const entriesFor = sectionType => {
    for (let [ key, value ] of Object.entries(floor.entriesBySection.items)) {
      if (key === sectionType) {
        return value
      }
    }
  }

  return (
    <div>

      <div className="mt-8">
        <div className="pt-3">

          <Accordion title="FLOOR CALENDAR BILLS">

          {entriesExist("RESOLUTIONS") &&
          <Accordion title="RESOLUTIONS">
            <ResultList results={entriesFor("RESOLUTIONS")} />
          </Accordion>
          }

          {entriesExist("ORDER_OF_THE_FIRST_REPORT") &&
          <Accordion title="BILLS ON ORDER OF FIRST REPORT">
            <ResultList results={entriesFor("ORDER_OF_THE_FIRST_REPORT")} />
          </Accordion>
          }

          {entriesExist("ORDER_OF_THE_SECOND_REPORT") &&
          <Accordion title="BILLS ON ORDER OF SECOND REPORT">
            <ResultList results={entriesFor("ORDER_OF_THE_SECOND_REPORT")} />
          </Accordion>
          }

          {entriesExist("ORDER_OF_THE_SPECIAL_REPORT") &&
          <Accordion title="BILLS ON ORDER OF SPECIAL REPORT">
            <ResultList results={entriesFor("ORDER_OF_THE_SPECIAL_REPORT")} />
          </Accordion>
          }

          {entriesExist("THIRD_READING_FROM_SPECIAL_REPORT") &&
          <Accordion title="BILLS ON THIRD READING FROM SPECIAL REPORT">
            <ResultList results={entriesFor("THIRD_READING_FROM_SPECIAL_REPORT")} />
          </Accordion>
          }

          {entriesExist("THIRD_READING") &&
          <Accordion title="BILLS ON THIRD READING">
            <ResultList results={entriesFor("THIRD_READING")} />
          </Accordion>
          }

          {entriesExist("STARRED_ON_THIRD_READING") &&
          <Accordion title="BILLS STARRED ON THIRD READING">
            <ResultList results={entriesFor("STARRED_ON_THIRD_READING")} />
          </Accordion>
          }
          </Accordion>

          {supplementalsExist &&
          // <Supplementals supplementalMap={supplementalMap}></Supplementals>

          <Accordion title="SUPPLEMENTAL CALENDAR BILLS">
            <div>
              {
                Object.entries(supplementals.items).map( supp => {
                  return <Supplementals supplemental={supp}></Supplementals>
                })
              }

            </div>
          </Accordion>
          }

        </div>
      </div>
    </div>
  )
}


function Supplementals({ supplemental }) {

  let supplementalItems = supplemental[1].entriesBySection.items

  const suppEntriesExist = (sectionType, supplementalItems) => {
    for (let [ key, value ] of Object.entries(supplementalItems)) {
      if (key === sectionType) {
        return true
      }
    }
  }

  const suppEntriesFor = (sectionType, supplementalItems) => {
    for (let [ key, value ] of Object.entries(supplementalItems)) {
      if (key === sectionType) {
        return value
      }
    }
  }



  return (
    <div>
      <h1>Supplemental {supplemental[1].version}</h1>

      {suppEntriesExist("RESOLUTIONS", supplementalItems) &&
      <Accordion title="RESOLUTIONS">
        <ResultList results={suppEntriesFor("RESOLUTIONS", supplementalItems)} />
      </Accordion>
      }

      {suppEntriesExist("ORDER_OF_THE_FIRST_REPORT", supplementalItems) &&
      <Accordion title="BILLS ON ORDER OF FIRST REPORT">
        <ResultList results={suppEntriesFor("ORDER_OF_THE_FIRST_REPORT", supplementalItems)} />
      </Accordion>
      }

      {suppEntriesExist("ORDER_OF_THE_SECOND_REPORT", supplementalItems) &&
      <Accordion title="BILLS ON ORDER OF SECOND REPORT">
        <ResultList results={suppEntriesFor("ORDER_OF_THE_SECOND_REPORT", supplementalItems)} />
      </Accordion>
      }

      {suppEntriesExist("ORDER_OF_THE_SPECIAL_REPORT", supplementalItems) &&
      <Accordion title="BILLS ON ORDER OF SPECIAL REPORT">
        <ResultList results={suppEntriesFor("ORDER_OF_THE_SPECIAL_REPORT", supplementalItems)} />
      </Accordion>
      }

      {suppEntriesExist("THIRD_READING_FROM_SPECIAL_REPORT", supplementalItems) &&
      <Accordion title="BILLS ON THIRD READING FROM SPECIAL REPORT">
        <ResultList results={suppEntriesFor("THIRD_READING_FROM_SPECIAL_REPORT", supplementalItems)} />
      </Accordion>
      }

      {suppEntriesExist("THIRD_READING", supplementalItems) &&
      <Accordion title="BILLS ON THIRD READING">
        <ResultList results={suppEntriesFor("THIRD_READING", supplementalItems)} />
      </Accordion>
      }

      {suppEntriesExist("STARRED_ON_THIRD_READING", supplementalItems) &&
      <Accordion title="BILLS STARRED ON THIRD READING">
        <ResultList results={suppEntriesFor("STARRED_ON_THIRD_READING", supplementalItems)} />
      </Accordion>
      }
    </div>
  )
}

function ResultList({ results }) {
  const list = results.items
  return (
    <div>
      {list.map((r) =>
        <BillListing bill={r}
                     highlights={r.title}
                     to={`/bills/${r.session}/${r.basePrintNo}`}
                     key={r.basePrintNoStr} />
      )}
    </div>
  )
}