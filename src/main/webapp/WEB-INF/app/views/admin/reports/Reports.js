import React from "react"
import {
  runSpotcheck,
  SpotcheckType
} from "app/apis/reportsApi.js";
import Select, { SelectOption } from "app/shared/Select";

const years = function () {
  const years = []
  for (let year = new Date().getFullYear(); year >= 2009; year--)
    years.push(year)
  return years
}()

// See SpotcheckCtrl and BillScrapeQueueCtrl
export default function Reports({ setHeaderText }) {
  const [ type, setType ] = React.useState(SpotcheckType.BOTH)
  const [ year, setYear ] = React.useState(years[0])
  const apiCall = (e) => {
     // TODO: show response in popup
    e.preventDefault()
    runSpotcheck(type, year).then(response => response.message)
  }
  return (
  <div>
    <h3 className="h4 mb-3">Run Reports or Add to Scrape Queue</h3>
    <form onSubmit={(e) => apiCall(e)} className="flex gap-6 items-end">
      <Select label="Spotcheck Report Type"
              value={type.name}
              options={SpotcheckType.types.map(type => new SelectOption(type.name, type.name))}
              onChange={(e) => setType(SpotcheckType.getType(e.target.value))}
              name="report type" />
      <Select label="Spotcheck Report Year"
              value={year}
              options={years.map(year => new SelectOption(year, year))}
              onChange={(e) => setYear(e.target.value)}
              name="report year" />
      <div>
        <button className="btn btn--primary w-36" type="submit">Run Report</button>
      </div>
    </form>
  </div>)
}
