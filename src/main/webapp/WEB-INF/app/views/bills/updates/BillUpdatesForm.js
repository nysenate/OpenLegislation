import React from "react"
import { DateTime } from "luxon";
import DatePicker from "app/shared/DatePicker";

/**
 * Contains the bill updates search form.
 */
export default function BillUpdatesForm({ doSearch, formData }) {
  const [ from, setFrom ] = React.useState(DateTime.fromISO(formData.from))
  const [ to, setTo ] = React.useState(DateTime.fromISO(formData.to))
  const typeRef = React.useRef()
  const orderRef = React.useRef()
  const filterRef = React.useRef()
  const detailRef = React.useRef()

  React.useEffect(() => {
    setFrom(DateTime.fromISO(formData.from))
    setTo(DateTime.fromISO(formData.to))
    typeRef.current.value = formData.type
    orderRef.current.value = formData.order
    filterRef.current.value = formData.filter
    detailRef.current.checked = formData.detail
  }, [ formData ])

  const onSubmit = (e) => {
    e.preventDefault()
    doSearch({
      from: from.startOf("day").toISO({ includeOffset: false }),
      to: to.endOf("day").toISO({ includeOffset: false }),
      order: orderRef.current.value,
      type: typeRef.current.value,
      filter: filterRef.current.value,
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
          <div className="flex flex-wrap items-end w-full lg:w-8/12">
            <div className={filterDivClasses}>
              <SearchLabel htmlFor="from">
                From
              </SearchLabel>
              <DatePicker id="from"
                          date={from}
                          setDate={(date) => setFrom(date)}
                          maxDate={to}
                          className={inputClassNames} />
            </div>
            <div className={filterDivClasses}>
              <SearchLabel htmlFor="to">
                To
              </SearchLabel>
              <DatePicker id="to"
                          date={to}
                          setDate={(date) => setTo(date)}
                          minDate={from}
                          maxDate={DateTime.local()}
                          className={inputClassNames} />
            </div>
            <div className={filterDivClasses}>
              <SearchLabel htmlFor="order">
                Sort by
              </SearchLabel>
              <select className={`select ${inputClassNames}`}
                      ref={orderRef}
                      id="order">
                <option value={ASC}>Oldest to Newest</option>
                <option value={DESC}>Newest to Oldest</option>
              </select>
            </div>
            <div className={filterDivClasses}>
              <SearchLabel htmlFor="type">
                Date Field
              </SearchLabel>
              <select ref={typeRef}
                      id="type"
                      className={`select ${inputClassNames}`}>
                {types.map((type) => <option value={type.value} key={type.value}>{type.label}</option>)}
              </select>
            </div>
            <div className={filterDivClasses}>
              <SearchLabel htmlFor="filter">
                Content Type
              </SearchLabel>
              <select className={`select ${inputClassNames}`} ref={filterRef} id="filter">
                <option value="">All</option>
                <option value="published_bill">Newly Published</option>
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
            <div className={filterDivClasses}>
              <div className={`flex items-center`}>
                <input type="checkbox"
                       ref={detailRef}
                       id="detail"
                       className="input mr-2" />
                <label htmlFor="detail" className="label">
                  Show Detail
                </label>
              </div>
            </div>
          </div>
          <div className="flex items-end w-full xl:w-4/12 mt-3">
            <button className="btn w-36 ml-auto">
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

const types = [
  {
    value: "published",
    label: "Published Date"
  },
  {
    value: "processed",
    label: "Processed Date"
  }
]

const ASC = "asc"
const DESC = "desc"
