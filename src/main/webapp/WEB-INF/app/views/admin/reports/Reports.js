import React from "react"
import {
  modifyScrapeQueue,
  runSpotcheck,
  SpotcheckType
} from "app/apis/reportsApi.js";
import Select, {
  SelectOption,
  yearSortOptions
} from "app/shared/Select";
import Input from "app/shared/Input";
import ErrorMessage from "app/shared/ErrorMessage";

const reportYears = yearSortOptions(2009, false, false)
const billSessionYears = yearSortOptions(2009, false, true)

export default function Reports({ setHeaderText }) {

  React.useEffect(() => {
    setHeaderText("Run Reports or Add to Scrape Queue")
  }, [])

  return (
    <div className="p-3">
      <ManualSpotchecks />
      <ModifyScrapeQueue addTo={true} />
      <ModifyScrapeQueue addTo={false} />
    </div>
  )
}

function ManualSpotchecks() {
  const [ type, setType ] = React.useState(SpotcheckType.BOTH)
  const [ year, setYear ] = React.useState(reportYears[0].value)
  const [ response, setResponse ] = React.useState({})
  const [ errorMsg, setErrorMsg ] = React.useState("")

  const apiCall = (e) => {
    e.preventDefault()
    setResponse({})
    setErrorMsg("")
    runSpotcheck(type, year).then(response => setResponse(response))
      .catch((error) => setErrorMsg(error.message))
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
                options={reportYears}
                onChange={(e) => setYear(e.target.value)}
                name="report year" />
        <div>
          <button className="btn btn--primary w-36" type="submit">Run Report</button>
        </div>
      </form>
      <div className="my-3">
        {errorMsg && <ErrorMessage>{errorMsg}</ErrorMessage>}
        {response?.message ? response.message : ""}
      </div>
    </div>)
}

function ModifyScrapeQueue({ addTo }) {
  const [ sessionYear, setSessionYear ] = React.useState(billSessionYears[0].value)
  const [ printNo, setPrintNo ] = React.useState("")
  const [ response, setResponse ] = React.useState({})
  const [ errorMsg, setErrorMsg ] = React.useState("")

  const getMessage = (response) => response.result ?
    `${response.result.basePrintNoStr} has been ${addTo ? "added to" : "removed from"} the queue.`
    : "";

  const apiCall = (e) => {
    e.preventDefault()
    setResponse({})
    setErrorMsg("")
    modifyScrapeQueue(addTo, sessionYear, printNo)
      .then(response => setResponse(response))
      .catch((error) => setErrorMsg(error.message))
  }

  return (
    <div>
      <h3 className="h4 mb-3"></h3>
      <form onSubmit={(e) => apiCall(e)} className="flex gap-6 items-end">
        <Input label={`${addTo ? "Add Bill To" : "Remove Bill From"} Scrape Queue`}
               onChange={(e) => setPrintNo(e.target.value)}
               value={printNo}
               tabIndex="1"
               name={`scrape queue ${addTo ? "add" : "remove"} print number`}
               type="text"
               placeholder="Print Number"
               className="w-full" />
        <Select label="Session Year"
                value={sessionYear}
                options={billSessionYears}
                onChange={(e) => setSessionYear(e.target.value)}
                name="session year" />
        <div>
          <button className="btn btn--primary w-36" type="submit">{addTo ? "Add" : "Remove"}</button>
        </div>
      </form>

      <div className="my-3">
        {errorMsg && <ErrorMessage>{"Bill number is incorrectly formatted."}</ErrorMessage>}
        {getMessage(response)}
      </div>
    </div>
  )
}
