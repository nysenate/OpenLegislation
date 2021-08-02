import React from 'react'
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import Accordion from "app/shared/Accordion";
import { billSessionYears } from "app/lib/dateUtils";
import { Sliders } from "phosphor-react";
import {
  billTypeOptions,
  chamberOptions
} from "app/views/bills/billSearchUtils";


const sortOptions = {
  relevant: "_score:desc,session:desc",
  recentStatusUpdate: "status.actionDate:desc,_score:desc",
  printNo: "printNo:asc,session:desc",
  mostProgress: "milestones.size:desc,_score:desc",
  mostAmendments: "amendments.size:desc,_score:desc",
}

const defaultSession = "Any"

const sessionYearEls = () => {
  return billSessionYears().map((y) => {
    return <option key={y} value={y}>{y}</option>
  })
}

const sponsorOptions = [ "Any", "John", "Bill", "Jill" ]

const advancedSearchTitleEls = (
  <div className="flex items-center">
    <Sliders size="1.25rem" className="inline-block mr-2" />
    <h4 className="inline-block">Advanced Search Options</h4>
  </div>
)

export default function BillSearchForm() {
  const [ term, setTerm ] = React.useState("")
  const [ sort, setSort ] = React.useState(sortOptions.relevant)
  const [ session, setSession ] = React.useState(defaultSession)
  const [ chamber, setChamber ] = React.useState(chamberOptions[0].value)
  const [ billType, setBillType ] = React.useState(billTypeOptions[0].value)
  const [ sponsor, setSponsor ] = React.useState(sponsorOptions[0])
  const location = useLocation()
  const history = useHistory()

  // Update search fields when back/forward navigation is used.
  React.useEffect(() => {
    const params = queryString.parse(location.search)
    setTerm(params.term || "")
    setSort(params.sort || sortOptions.relevant)
    setSession(params.session || defaultSession)
    setChamber(params.chamber || chamberOptions[0].value)
    setBillType(params.billType || billTypeOptions[0].value)
  }, [ location ])

  // Updates the term query param when the form is submitted.
  const onSubmit = (e) => {
    e.preventDefault()
    const params = queryString.parse(location.search)
    params.term = term
    params.sort = sort
    params.session = session
    params.chamber = chamber
    params.billType = billType
    console.log(params)
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div className="flex flex-wrap">
          <div className="flex-grow mr-8">
            <label htmlFor="billsearch" className="label label--top">
              Print number or term
            </label>
            <input onChange={(e) => setTerm(e.target.value)}
                   value={term}
                   tabIndex="1"
                   name="billsearch"
                   type="text"
                   className="input w-full"
                   placeholder="e.g. S1234-2015 or yogurt" />
          </div>
          <div className="mr-8">
            <label htmlFor="session-year-select" className="label label--top">Session Year</label>
            <select id="session-year-select"
                    tabIndex="2"
                    value={session}
                    onChange={(e) => setSession(e.target.value)}
                    className="select w-full">
              <option value="Any">Any</option>
              {sessionYearEls()}
            </select>
          </div>
          <div className="">
            <label htmlFor="sort-by-select" className="label label--top">Sort By</label>
            <select id="sort-by-select"
                    tabIndex="3"
                    value={sort}
                    onChange={(e) => setSort(e.target.value)}
                    className="select w-full">
              <option value={sortOptions.relevant}>Relevant</option>
              <option value={sortOptions.recentStatusUpdate}>Recent Status Update</option>
              <option value={sortOptions.printNo}>Print No</option>
              <option value={sortOptions.mostProgress}>Most Progress</option>
              <option value={sortOptions.mostAmendments}>Most Amendments</option>
            </select>
          </div>
        </div>

        <div className="m-4">
          <Accordion title={advancedSearchTitleEls}>
            <div>

              {/*// TODO how do these modify the api call when set?*/}
              <SearchSelect label="Chamber"
                                    value={chamber}
                                    onChange={(e) => setChamber(e.target.value)}
                                    options={chamberOptions} />

              <SearchSelect label="Bill/Resolution"
                                    value={billType}
                                    onChange={(e) => setBillType(e.target.value)}
                                    options={billTypeOptions} />

              {/*<AdvancedSearchSelect label="Primary Sponsor"*/}
              {/*                      value={sponsor}*/}
              {/*                      onChange={(e) => setSponsor(e.target.value)}*/}
              {/*                      options={sponsorOptions} />*/}

            </div>
          </Accordion>
        </div>

        <div className="flex justify-end">
          <button className="btn my-3 w-36" type="submit" tabIndex="4">Search</button>
        </div>
      </form>
    </div>
  )
}

function SearchSelect({ label, value, onChange, options }) {
  return (
    <div className="w-2/12 m-2">
      <label className="label label--top">{label}
        <select value={value}
                onChange={onChange}
                className="select w-full">
          {options.map((opt) => <option value={opt.value} key={opt.value}>{opt.label}</option>)}
        </select>
      </label>
    </div>
  )
}

