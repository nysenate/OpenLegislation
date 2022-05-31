import React from 'react'
import {
  useHistory,
  useLocation
} from "react-router-dom";
import queryString from "query-string";
import DatePicker from "app/shared/DatePicker";
import { DateTime } from "luxon";
import Select, {
  SelectOption,
  sortOptions
} from "app/shared/Select";


export default function AgendaUpdatesForm({ from, to, type = "published", detail = true, sort = "desc" }) {
  const [ dirtyFrom, setDirtyFrom ] = React.useState(from)
  const [ dirtyTo, setDirtyTo ] = React.useState(to)
  const [ dirtyType, setDirtyType ] = React.useState(type)
  const [ dirtyDetail, setDirtyDetail ] = React.useState(detail)
  const [ dirtySort, setDirtySort ] = React.useState(sort)
  const { search } = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    setDirtyFrom(from)
    setDirtyTo(to)
    setDirtyType(type)
    setDirtyDetail(detail)
    setDirtySort(sort)
  }, [ from, to, type, detail, sort ])

  const onSubmit = e => {
    e.preventDefault()
    const params = queryString.parse(search)
    params.from = dirtyFrom.toISODate()
    params.to = dirtyTo.toISODate()
    params.type = dirtyType
    params.detail = dirtyDetail.toString()
    params.sort = dirtySort
    params.page = 1 // Reset to page 1 when starting a new search.
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div className="flex flex-wrap items-center gap-3">
          <div>
            <DatePicker label="From"
                        name="from"
                        date={dirtyFrom}
                        setDate={d => setDirtyFrom(d)}
                        maxDate={dirtyTo}
                        selectsStart />
          </div>
          <div>
            <DatePicker label="To"
                        name="to"
                        date={dirtyTo}
                        setDate={d => setDirtyTo(d)}
                        minDate={dirtyFrom}
                        maxDate={DateTime.now()}
                        selectsEnd />
          </div>
          <Select label="Date Field"
                  name="type"
                  value={dirtyType}
                  options={typeOptions}
                  onChange={e => setDirtyType(e.target.value)} />
          <Select label="Sort By"
                  value={dirtySort}
                  onChange={e => setDirtySort(e.target.value)}
                  options={sortOptions}
                  name="sort" />
          <div>
            <div className="h-7"></div>
            <div className="flex items-center">
              <input type="checkbox"
                     id="detail"
                     name="detail"
                     checked={dirtyDetail}
                     onChange={e => setDirtyDetail(e.target.checked)} />
              <label htmlFor="detail" className="label ml-1">
                Show Detail
              </label>
            </div>
          </div>
        </div>

        <div className="flex justify-end mt-3">
          <button className="btn btn--primary w-36" type="submit">Submit</button>
        </div>
      </form>
    </div>
  )
}

const typeOptions = [
  new SelectOption("published", "Published Date"),
  new SelectOption("processed", "Processed Date"),
]
