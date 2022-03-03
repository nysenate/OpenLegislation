import React from 'react'
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import DatePicker from "app/shared/DatePicker";
import { DateTime } from "luxon";
import Select, {
  SelectOption,
  sortOptions
} from "app/shared/Select";


export default function LawSearchForm({ updateValues, from, to }) {
  // "Dirty" values hold the user set values until the form is submitted.
  const [ dirtyFrom, setDirtyFrom ] = React.useState(from)
  const [ dirtyTo, setDirtyTo ] = React.useState(to)
  const [ dirtyType, setDirtyType ] = React.useState(dateTypeOptions[0].value)
  const [ dirtySort, setDirtySort ] = React.useState(sortOptions[1].value)

  const onSubmitLawUpdatesSearch = (e) => {
    e.preventDefault()
    updateValues(dirtyFrom, dirtyTo, dirtyType, dirtySort);
  }

  return (
    <div>
      <form onSubmit={onSubmitLawUpdatesSearch}>
        <h3 className="mb-3">Search law updates</h3>
        <div className="flex flex-wrap">
          <div className="mr-3">
            <DatePicker label="From"
                        name="from"
                        date={dirtyFrom}
                        setDate={(date) => setDirtyFrom(date)}
                        maxDate={dirtyTo}
                        tabIndex={1}
            />
          </div>
          <div className="mr-3">
            <DatePicker label="To"
                        name="to"
                        date={dirtyTo}
                        setDate={(date) => setDirtyTo(date)}
                        minDate={dirtyFrom}
                        maxDate={DateTime.now()}
                        tabIndex={2}
            />
          </div>
          <div className="mr-3">
            <Select label="Date Type"
                    value={dirtyType}
                    options={dateTypeOptions}
                    onChange={(e) => setDirtyType(e.target.value)}
                    name="field"
                    tabIndex={3}
            />
          </div>
          <div className="mr-3">
            <Select label="Sort By"
                    value={dirtySort}
                    options={sortOptions}
                    onChange={(e) => setDirtySort(e.target.value)}
                    name="sortBy"
                    tabIndex={4}
            />
          </div>
        </div>

        <div className="flex justify-end">
          <button className="btn btn--primary my-3 w-36" type="submit" tabIndex="5">Search</button>
        </div>
      </form>
    </div>
  )
}

const dateTypeOptions = [
  new SelectOption("published", "Published Date"),
  new SelectOption("processed", "Processed Date")
]
