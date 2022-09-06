import React from 'react'
import Input from "app/shared/Input";
import Select, {
  sortOptions,
  yearSortOptions
} from "app/shared/Select";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import queryString from "query-string";


export default function CalendarSearchForm({
                                             year,
                                             calendarNo = "",
                                             printNo = "",
                                             billCalendarNo = "",
                                             sort = "desc"
                                           }) {
  const [ dirtyYear, setDirtyYear ] = React.useState(year)
  const [ dirtyCalendarNo, setDirtyCalendarNo ] = React.useState(calendarNo)
  const [ dirtyPrintNo, setDirtyPrintNo ] = React.useState(printNo)
  const [ dirtyBillCalendarNo, setDirtyBillCalendarNo ] = React.useState(billCalendarNo)
  const [ dirtySort, setDirtySort ] = React.useState(sort)
  const { search } = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    setDirtyYear(year)
    setDirtyCalendarNo(calendarNo)
    setDirtyPrintNo(printNo)
    setDirtyBillCalendarNo(billCalendarNo)
    setDirtySort(sort)
  }, [ year, calendarNo, printNo, billCalendarNo, sort ])

  const onSubmit = e => {
    e.preventDefault()
    const params = queryString.parse(search)
    params.year = dirtyYear
    params.calendarNo = dirtyCalendarNo
    params.printNo = dirtyPrintNo
    params.billCalendarNo = dirtyBillCalendarNo
    params.sort = dirtySort
    params.page = 1 // Reset page to 1 when starting a new search.
    history.push({ search: queryString.stringify(params) })
  }

  return (<div>
    <form onSubmit={onSubmit} className="mb-3">
      <div className="flex gap-x-6 gap-y-3 flex-wrap">
        <Select label="Year"
                value={dirtyYear}
                options={yearSortOptions(2009, false, false)}
                onChange={e => setDirtyYear(e.target.value)}
                name="year" />
        <Input label="Calendar Number"
               value={dirtyCalendarNo}
               onChange={e => setDirtyCalendarNo(e.target.value)}
               name="calendarNumber" />
        <Input label="Print No"
               value={dirtyPrintNo}
               onChange={e => setDirtyPrintNo(e.target.value)}
               name="printNo" />
        <Input label="Bill Calendar Number"
               value={dirtyBillCalendarNo}
               onChange={e => setDirtyBillCalendarNo(e.target.value)}
               name="billCalendarNumber" />
        <Select label="Sort By"
                value={dirtySort}
                onChange={e => setDirtySort(e.target.value)}
                options={sortOptions}
                name="sort" />
      </div>
      <div className="flex justify-end mt-3">
        <button className="btn btn--primary w-36" type="submit">Submit</button>
      </div>
    </form>
  </div>)
}
