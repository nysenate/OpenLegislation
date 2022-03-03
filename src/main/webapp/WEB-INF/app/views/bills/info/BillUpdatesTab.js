import React from "react"
import LoadingIndicator from "app/shared/LoadingIndicator";
import { fetchSingleBillUpdates } from "app/apis/billGetApi";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import BillUpdate from "app/shared/BillUpdate";
import Select, {
  SelectOption,
  sortOptions
} from "app/shared/Select";


export default function BillUpdatesTab({ bill }) {
  const [ updates, setUpdates ] = React.useState()
  const [ updateType, setUpdateType ] = React.useState("status")
  const [ sort, setSort ] = React.useState()
  const location = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    setUpdates(undefined)
    fetchSingleBillUpdates(bill.session, bill.printNo, { order: sort, filter: updateType, offset: 1, limit: 1000 })
      .then((res) => {
        setUpdates(res.result.items)
      })
  }, [ bill, updateType, sort ])

  React.useEffect(() => {
    const params = queryString.parse(location.search, { parseBooleans: true })
    setSort(params.sort)
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
      <div className="w-44">
        <Select label="Update Type"
                value={updateType}
                onChange={(e) => onUpdateTypeChange(e.target.value)}
                className="select"
                name="updateType"
                options={updateTypeOptions} />
      </div>
      <div>
        <Select label="Sort By"
                value={sort}
                onChange={(e) => onSortChange(e.target.value)}
                options={sortOptions}
                name="sort" />
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
            <BillUpdate update={update} />
          </div>
        )
      })}
    </React.Fragment>
  )
}

const updateTypeOptions = [
  new SelectOption("", "All"),
  new SelectOption("action", "Action"),
  new SelectOption("active_version", "Active Version"),
  new SelectOption("approval", "Approval Memo"),
  new SelectOption("cosponsor", "Co Sponsor"),
  new SelectOption("act_clause", "Enacting Clause"),
  new SelectOption("fulltext", "Full Text"),
  new SelectOption("law", "Law"),
  new SelectOption("memo", "Memo"),
  new SelectOption("multisponsor", "Multi Sponsor"),
  new SelectOption("sponsor", "Sponsor"),
  new SelectOption("status", "Status"),
  new SelectOption("summary", "Summary"),
  new SelectOption("title", "Title"),
  new SelectOption("veto", "Veto"),
  new SelectOption("vote", "Vote"),
]
