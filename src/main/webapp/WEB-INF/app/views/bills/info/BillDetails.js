import React from 'react'
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";

export default function BillDetails({ bill }) {
  const [ version, setVersion ] = React.useState(bill.activeVersion)

  return (
    <div>
      <AmendmentSwitcher bill={bill} version={version} setVersion={setVersion} />
      <hr />
    </div>
  )
}

function AmendmentSwitcher({ bill, version, setVersion }) {
  if (bill?.amendments?.size <= 1) {
    return null
  }

  return (
    <React.Fragment>
      <hr />
      <div className="m-3">
        <label className="flex items-center">
          <h4 className="h5 mr-3">Amendment Version</h4>
          <select value={version} onChange={(e) => {setVersion(e.target.value)}} className="select">
            {Object.entries(bill.amendments.items).map(([ key, amd ]) => {
              let label = amd.version === "" ? "Original" : `Revision ${amd.version}`
              if (amd.version === bill.activeVersion) {
                label += " (Latest)"
              }
              return (
                <option value={amd.version} key={key}>
                  {label} - {formatDateTime(amd.publishDate, DateTime.DATE_MED)}
                </option>
              )
            })}
          </select>
        </label>
      </div>
    </React.Fragment>
  )
}
