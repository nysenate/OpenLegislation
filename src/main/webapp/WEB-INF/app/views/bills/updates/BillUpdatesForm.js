import React from "react"
import { DateTime } from "luxon";
import DatePicker from "app/shared/DatePicker";
import Select, {
  SelectOption,
  sortOptions
} from "app/shared/Select";

/**
 * Contains the bill updates search form.
 * @param doSearch Callback
 * @param formData Current/initial form values taken from the url. This ensures the form has the correct values
 *                 on forward/back browser navigation.
 */
export default function BillUpdatesForm({ doSearch, formData }) {
  const [ from, setFrom ] = React.useState(DateTime.fromISO(formData.from))
  const [ to, setTo ] = React.useState(DateTime.fromISO(formData.to))
  const [ type, setType ] = React.useState(formData.type)
  const [ order, setOrder ] = React.useState(formData.order)
  const [ filter, setFilter ] = React.useState(formData.filter)
  const detailRef = React.useRef()

  React.useEffect(() => {
    setFrom(DateTime.fromISO(formData.from))
    setTo(DateTime.fromISO(formData.to))
    detailRef.current.checked = formData.detail
  }, [ formData ])

  const onSubmit = (e) => {
    e.preventDefault()
    doSearch({
      from: from.startOf("day").toISO({ includeOffset: false }),
      to: to.endOf("day").toISO({ includeOffset: false }),
      order: order,
      type: type,
      filter: filter,
      detail: detailRef.current.checked
    })
  }

  const filterDivClasses = "w-6/12 mt-3 lg:w-4/12"
  const inputClassNames = "w-40"

  return (
    <div>
      <p>
        Show Bill updates during the following date range
      </p>
      <form onSubmit={onSubmit}>
        <div className="flex flex-wrap">
          <div className="flex flex-wrap items-end lg:w-8/12">
            <div className={filterDivClasses}>
              <DatePicker label="From"
                          name="fromdatetime"
                          date={from}
                          setDate={(date) => setFrom(date)}
                          maxDate={to}
                          selectsStart
                          className={inputClassNames} />
            </div>
            <div className={filterDivClasses}>
              <DatePicker label="To"
                          name="todatetime"
                          date={to}
                          setDate={(date) => setTo(date)}
                          minDate={from}
                          maxDate={DateTime.now()}
                          selectsEnd
                          className={inputClassNames} />
            </div>
            <div className={filterDivClasses}>
              <Select label="Sort By"
                      value={order}
                      options={sortOptions}
                      onChange={(e) => {
                        setOrder(e.target.value)
                      }}
                      name="order"
                      className={inputClassNames} />
            </div>
            <div className={filterDivClasses}>
              <Select label="Date Field"
                      value={type}
                      options={typeOptions}
                      onChange={(e) => {
                        setType(e.target.value)
                      }}
                      name="type"
                      className={inputClassNames} />
            </div>
            <div className={filterDivClasses}>
              <Select label="Content Type"
                      value={filter}
                      options={contentTypeOptions}
                      onChange={(e) => {
                        setFilter(e.target.value)
                      }}
                      name="filter"
                      className={inputClassNames} />
            </div>
            <div className={filterDivClasses}>
              <div className={`flex items-center`}>
                <input type="checkbox"
                       ref={detailRef}
                       id="detail"
                       className="mr-2 cursor-pointer" />
                <label htmlFor="detail" className="label">
                  Show Detail
                </label>
              </div>
            </div>
          </div>
          <div className="flex items-end w-full xl:w-4/12 mt-3">
            <button className="btn btn--primary w-36 ml-auto">
              Submit
            </button>
          </div>
        </div>
      </form>
    </div>
  )
}

function SearchLabel({ htmlFor, children }) {
  return (
    <label className="label label--top" htmlFor={htmlFor}>
      {children}
    </label>
  )
}

const typeOptions = [
  new SelectOption("published", "Published Date"),
  new SelectOption("processed", "Processed Date")
]

const contentTypeOptions = [
  new SelectOption("", "All"),
  new SelectOption("published_bill", "Newly Published"),
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
