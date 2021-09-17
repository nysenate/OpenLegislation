import React from "react"
import LoadingIndicator from "app/shared/LoadingIndicator";
import { getBillUpdatesApi } from "app/apis/billGetApi";
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import {
  Link,
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";

const ASC = "asc"
const DESC = "desc"

export default function BillUpdatesTab({ bill }) {
  const [ updates, setUpdates ] = React.useState()
  const [ updateType, setUpdateType ] = React.useState("status")
  const [ sort, setSort ] = React.useState(DESC)
  const location = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    setUpdates(undefined)
    getBillUpdatesApi(bill.session, bill.printNo, { order: sort, filter: updateType, offset: 1, limit: 1000 })
      .then((res) => {
        setUpdates(res.items)
      })
  }, [ bill, updateType, sort ])

  React.useEffect(() => {
    const params = queryString.parse(location.search, { parseBooleans: true })
    setSort(params.sort || DESC)
    setUpdateType((params.updateType == null) ? "status" : params.updateType)
  }, [ location.search ])

  const onUpdateTypeChange = (value) => {
    const params = queryString.parse(location.search)
    params.updateType = value
    history.push({ search: queryString.stringify(params) })
  }

  const onSortChange = (value) => {
    const params = queryString.parse(location.search)
    params.sort = value
    history.push({ search: queryString.stringify(params) })
  }

  if (!updates) {
    return <LoadingIndicator />
  }

  return (
    <section className="m-5">
      <div className="my-5">
        <Filters updateType={updateType}
                 onUpdateTypeChange={onUpdateTypeChange}
                 sort={sort}
                 onSortChange={onSortChange} />
      </div>
      <div>
        <div className="text-center">
          <span className="font-semibold">{updates.length}</span> matching updates were found.
        </div>
        <div>
          <UpdateList updates={updates} />
        </div>
      </div>
    </section>
  )
}

function Filters({ updateType, onUpdateTypeChange, sort, onSortChange }) {
  return (
    <div className="flex flex-wrap gap-x-8">
      <div>
        <label className="label label--top" htmlFor="updateType">Update type</label>
        <select value={updateType}
                onChange={(e) => onUpdateTypeChange(e.target.value)}
                className="select"
                name="updateType">
          <option value="">All</option>
          <option value="action">Action</option>
          <option value="active_version">Active Version</option>
          <option value="approval">Approval Memo</option>
          <option value="cosponsor">Co Sponsor</option>
          <option value="act_clause">Enacting Clause</option>
          <option value="fulltext">Full Text</option>
          <option value="law">Law</option>
          <option value="memo">Memo</option>
          <option value="multisponsor">Multi Sponsor</option>
          <option value="sponsor">Sponsor</option>
          <option value="status">Status</option>
          <option value="summary">Summary</option>
          <option value="title">Title</option>
          <option value="veto">Veto</option>
          <option value="vote">Vote</option>
        </select>
      </div>
      <div>
        <label className="label label--top" htmlFor="sort">Sort by</label>
        <select value={sort}
                onChange={(e) => onSortChange(e.target.value)}
                className="select"
                name="sort">
          <option value={ASC}>Oldest to Newest</option>
          <option value={DESC}>Newest to Oldest</option>
        </select>
      </div>
    </div>
  )
}

function UpdateList({ updates }) {
  return (
    <React.Fragment>
      {updates.map((update, index) => {
        return (
          <div className="py-6 border-b-1 border-gray-300" key={index}>
            <Update update={update} />
          </div>
        )
      })}
    </React.Fragment>
  )
}

function Update({ update }) {
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
          <Link to={`/api/3/sources/fragment/${update.sourceId}`}
                target="_blank">{update.sourceId}</Link>
        </div>
        <div className="mt-3">
          <FieldTable update={update} />
        </div>
      </div>
    </React.Fragment>
  )
}

function FieldTable({ update }) {
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