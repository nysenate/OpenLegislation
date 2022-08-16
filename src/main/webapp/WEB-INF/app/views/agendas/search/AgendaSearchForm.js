import React from 'react'
import Input from "app/shared/Input";
import Select, {
  SelectOption,
  sortOptions,
} from "app/shared/Select";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import DatePicker from "app/shared/DatePicker";
import { DateTime } from "luxon";
import queryString from "query-string";


export default function AgendaSearchForm({ from, to, committee = "", printNo = "", notes = "", sort = "desc" }) {
  const [ dirtyFrom, setDirtyFrom ] = React.useState(from)
  const [ dirtyTo, setDirtyTo ] = React.useState(to)
  const [ dirtyPrintNo, setDirtyPrintNo ] = React.useState(printNo)
  const [ dirtyCommittee, setDirtyCommittee ] = React.useState(committee)
  const [ dirtyNotes, setDirtyNotes ] = React.useState(notes)
  const [ dirtySort, setDirtySort ] = React.useState(sort)
  const { search } = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    setDirtyFrom(from)
    setDirtyTo(to)
    setDirtyPrintNo(printNo)
    setDirtyCommittee(committee)
    setDirtyNotes(notes)
    setDirtySort(sort)
  }, [ from, to, printNo, committee, notes, sort ])

  const onSubmit = e => {
    e.preventDefault()
    const params = queryString.parse(search)
    params.from = dirtyFrom.toISODate()
    params.to = dirtyTo.toISODate()
    params.committee = dirtyCommittee
    params.printNo = dirtyPrintNo
    params.notes = dirtyNotes
    params.sort = dirtySort
    params.page = 1 // Reset to page 1 when starting a new search.
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="mb-3">
      <form onSubmit={onSubmit}>
        <div className="flex gap-x-6 gap-y-3 flex-wrap">
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
          <Select label="Committee"
                  value={dirtyCommittee}
                  options={committeeOptions}
                  onChange={e => setDirtyCommittee(e.target.value)}
                  name="committee" />
          <Select label="Sort By"
                  value={dirtySort}
                  onChange={e => setDirtySort(e.target.value)}
                  options={sortOptions}
                  name="sort" />
          <Input label="Base Print No"
                 value={dirtyPrintNo}
                 onChange={(e) => setDirtyPrintNo(e.target.value)}
                 placeholder="e.g. S100"
                 name="printNo" />
          <div className="">
            <Input label="Meeting Notes"
                   value={dirtyNotes}
                   onChange={(e) => setDirtyNotes(e.target.value)}
                   placeholder="e.g. Off the floor"
                   name="notes" />
          </div>
        </div>
        <div className="flex justify-end mt-3">
          <button className="btn btn--primary w-36" type="submit">Submit</button>
        </div>
      </form>
    </div>
  )
}

const committeeOptions = [
  new SelectOption("any", "Any"),
  new SelectOption("Aging", "Aging"),
  new SelectOption("Agriculture", "Agriculture"),
  new SelectOption("Alcoholism And Substance Abuse", "Alcoholism And Substance Abuse"),
  new SelectOption("Banks", "Banks"),
  new SelectOption("Budget And Revenue", "Budget And Revenue"),
  new SelectOption("Children And Families", "Children And Families"),
  new SelectOption("Cities", "Cities"),
  new SelectOption("Civil Service And Pensions", "Civil Service And Pensions"),
  new SelectOption("Codes", "Codes"),
  new SelectOption("Commerce, Economic Development And Small Business", "Commerce, Economic Development And Small Business"),
  new SelectOption("Consumer Protection", "Consumer Protection"),
  new SelectOption("Corporations, Authorities And Commissions", "Corporations, Authorities And Commissions"),
  new SelectOption("Crime Victims, Crime And Correction", "Crime Victims, Crime And Correction"),
  new SelectOption("Cultural Affairs, Tourism, Parks And Recreation", "Cultural Affairs, Tourism, Parks And Recreation"),
  new SelectOption("Domestic Animal Welfare", "Domestic Animal Welfare"),
  new SelectOption("Education", "Education"),
  new SelectOption("Elections", "Elections"),
  new SelectOption("Energy And Telecommunications", "Energy And Telecommunications"),
  new SelectOption("Ethics And Internal Governance", "Ethics And Internal Governance"),
  new SelectOption("Finance", "Finance"),
  new SelectOption("Health", "Health"),
  new SelectOption("Higher Education", "Higher Education"),
  new SelectOption("Housing, Construction And Community Development", "Housing, Construction And Community Development"),
  new SelectOption("Insurance", "Insurance"),
  new SelectOption("Internet And Technology", "Internet And Technology"),
  new SelectOption("Investigations And Government Operations", "Investigations And Government Operations"),
  new SelectOption("Judiciary", "Judiciary"),
  new SelectOption("Labor", "Labor"),
  new SelectOption("Local Government", "Local Government"),
  new SelectOption("Mental Health And Development Disabilities", "Mental Health And Development Disabilities"),
  new SelectOption("New York City Education", "New York City Education"),
  new SelectOption("Racing, Gaming And Wagering", "Racing, Gaming And Wagering"),
  new SelectOption("Rules", "Rules"),
  new SelectOption("Social Services", "Social Services"),
  new SelectOption("Transportation", "Transportation"),
  new SelectOption("Veterans, Homeland Security And Military Affairs", "Veterans, Homeland Security And Military Affairs"),
  new SelectOption("Women's Issues", "Women's Issues")
]
