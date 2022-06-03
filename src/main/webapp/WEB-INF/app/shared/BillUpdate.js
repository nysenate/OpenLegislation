import React from 'react'
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import { Link } from "react-router-dom";
import UpdateFieldTable from "app/shared/UpdateFieldTable";


export default function BillUpdate({ update, linkSource = true }) {
  return (
    <React.Fragment>
      <div className="mb-1">
        <span className="font-semibold">{update.action}</span> | <span className="font-semibold">{update.scope}</span>
      </div>
      <div>
        <div>
          <span className="font-light mr-1.5">Published date time:</span> {formatDateTime(update.sourceDateTime, DateTime.DATETIME_MED)}
        </div>
        <div>
          <span className="font-light">Processed date time:</span> {formatDateTime(update.processedDateTime, DateTime.DATETIME_MED)}
        </div>
        <div>
          <span className="font-light">Source:</span>&nbsp;
          <Source sourceId={update.sourceId} linkSource={linkSource} />
        </div>
        <div className="mt-3">
          <UpdateFieldTable updateFields={update.fields} />
        </div>
      </div>
    </React.Fragment>
  )
}

function Source({ sourceId, linkSource }) {
  if (!linkSource) {
    return (
      <span>{sourceId}</span>
    )
  }
  return (
    <Link to={`/api/3/sources/fragment/${sourceId}`} target="_blank" className="link">{sourceId}</Link>
  )
}
