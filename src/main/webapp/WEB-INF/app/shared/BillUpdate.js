import React from 'react'
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import { Link } from "react-router-dom";


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
          <FieldTable update={update} />
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

function FieldTable({ update }) {
  if (!update.fields) {
    return null
  }
  return (
    <table className="table table--stripe">
      <thead>
      <tr>
        <th>Field Name</th>
        <th>Data</th>
      </tr>
      </thead>
      <tbody>
      {Object.entries(update.fields).map(([ key, value ]) => {
        return (
          <tr key={key}>
            <td>{key}</td>
            <td>
              <pre className="whitespace-pre-wrap">{value}</pre>
            </td>
          </tr>
        )
      })}
      </tbody>
    </table>
  )
}
