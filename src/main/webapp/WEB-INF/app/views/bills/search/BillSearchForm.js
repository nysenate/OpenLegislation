import React from 'react'
import {
  fetchMembers,
  fetchStatusTypes,
  initialRefineState,
  sessionOptions,
  sortOptions
} from "app/views/bills/search/billSearchUtils";
import * as queryString from "query-string";
import Accordion from "app/shared/Accordion";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import { Sliders } from "phosphor-react";
import Input from "app/shared/Input";
import Select from "app/shared/Select";


/**
 * This reducer handles updating the refine object which contains all the advanced filter objects.
 *
 * @param state the current state.
 * @param action Describes the action being performed. Has type, value, and key fields. type is the type of
 * action being performed, value is the new value, and key is the name of a field on the refine object - this
 * controls which filter on refine is modified by the action.
 * @returns {*}
 */
const registerReducer = function (state, action) {
  switch (action.type) {
    case "update":
      return {
        ...state,
        [action.key]: {
          ...state[action.key],
          value: action.value
        }
      }
    case "setOptions":
      return {
        ...state,
        [action.key]: {
          ...state[action.key],
          options: action.value
        }
      }
  }
}


export default function BillSearchForm() {
  const [ term, setTerm ] = React.useState("")
  const [ sort, setSort ] = React.useState(sortOptions[0].value)
  const [ session, setSession ] = React.useState(sessionOptions[0].value)
  const [ refine, dispatch ] = React.useReducer(registerReducer, initialRefineState)
  const location = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    fetchStatusTypes().then((types) => {
      dispatch({
        type: "setOptions",
        key: "statusType",
        value: types
      })
    })
  }, [])

  React.useEffect(() => {
    fetchMembers(session).then((members) => {
      dispatch({
        type: "setOptions",
        key: "sponsor",
        value: members
      })
    })
  }, [ session ])

  /**
   * Whenever the URL changes, update all filters to the values from the url params.
   * This allows back/forward browser navigation and sharing of links to specific search results.
   */
  React.useEffect(() => {
    const params = queryString.parse(location.search, { parseBooleans: true })
    setTerm(params.term || "")
    setSession(params.session || sessionOptions[0].value)
    setSort(params.sort || sortOptions[0].value)
    Object.entries(refine).forEach(([ key, value ]) => {
      dispatch({
        type: "update",
        key: key,
        value: params[key] || ""
      })
    })
  }, [ location ])

  /**
   * Update the url params when a search is performed.
   * A search will be triggered in the parent Component, BillSearch, when the url is updated.
   */
  const onSubmit = (e) => {
    e.preventDefault()
    const params = queryString.parse(location.search)
    params.term = term
    params.session = session
    params.sort = sort
    params.page = 1 // Reset page
    Object.entries(refine).forEach(([ key, value ]) => {
      params[key] = value.value
    })
    history.push({ search: queryString.stringify(params) })
  }

  const resetFilters = () => {
    Object.entries(refine).forEach(([ key, value ]) => {
      dispatch({
        type: "update",
        key: key,
        value: ""
      })
    })
  }

  const advancedSearchTitleEls = (
    <div className="flex items-center">
      <Sliders size="1.25rem" className="inline-block mr-2" />
      <h4 className="inline-block">Advanced Search Options</h4>
    </div>
  )

  const filterWrapperClass = "mx-4 my-2"
  const advancedFilterColumnClass = "flex flex-col w-12/12 sm:w-6/12 lg:w-3/12"

  const advancedSelectEls = () => {
    return Object.entries(refine).filter(([ key, value ]) => {
        return value.type === "select"
      })
      .map(([ key, value ]) => {
        if (key === 'sponsor') {
          let labelClasses = "label label--top"
          labelClasses += value.value ? " bg-yellow-100" : "" // if value is set, highlight label.
          // Sponsor select needs custom grouping by chamber.
          const other = value.options.filter(o => o.chamber == null)
          const senators = value.options.filter(o => o.chamber === 'SENATE')
          const assemblymen = value.options.filter(o => o.chamber === 'ASSEMBLY')
          return (
            <div className={filterWrapperClass} key={key}>
              <label className={labelClasses}>
                {value.label}
                <select value={value.value}
                        onChange={(e) => dispatch({
                          type: "update",
                          value: e.target.value,
                          key: key
                        })}
                        className="select block w-full">
                  {other.map(m => <option value={m.value} key={m.value}>{m.label}</option>)}
                  <optgroup label="Senators">
                    {senators.map(m => <option value={m.value} key={m.value}>{m.label}</option>)}
                  </optgroup>
                  <optgroup label="Assemblymen">
                    {assemblymen.map(m => <option value={m.value} key={m.value}>{m.label}</option>)}
                  </optgroup>
                </select>
              </label>
            </div>
          )
        } else {
          return (
            <div className={filterWrapperClass} key={key}>
              <Select label={value.label}
                      value={value.value}
                      onChange={(e) => dispatch({
                        type: "update",
                        value: e.target.value,
                        key: key
                      })}
                      options={value.options}
                      isHighlighted={value.value}
                      className="w-full"
              />
            </div>
          )
        }
      })
  }

  const advancedInputEls = () => {
    return Object.entries(refine).filter(([ key, value ]) => {
        return value.type === "input"
      })
      .map(([ key, value ]) => {
        return (
          <div className={filterWrapperClass} key={key}>
            <Input label={value.label}
                   value={value.value}
                   onChange={(e) => dispatch({
                     type: "update",
                     value: e.target.value,
                     key: key
                   })}
                   placeholder={value.placeholder}
                   isHighlighted={value.value}
                   type="text"
                   className="w-full"
            />
          </div>
        )
      })
  }

  const advancedCheckboxEls = () => {
    return Object.entries(refine).filter(([ key, value ]) => {
        return value.type === "checkbox"
      })
      .map(([ key, value ]) => {
        return (
          <div className={filterWrapperClass} key={key}>
            <SearchCheckbox label={value.label}
                            value={value.value}
                            onChange={(e) => dispatch({
                              type: "update",
                              value: e.target.checked,
                              key: key
                            })} />
          </div>
        )
      })
  }

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div className="flex flex-wrap">
          <div className="flex-grow mr-8">
            <Input label="Print number or term"
                   onChange={(e) => setTerm(e.target.value)}
                   value={term}
                   tabIndex="1"
                   name="billsearch"
                   type="text"
                   placeholder="e.g. S1234-2015 or yogurt"
                   className="w-full" />
          </div>
          <div className="mr-8">
            <Select label="Session Year"
                    tabIndex="2"
                    value={session}
                    onChange={(e) => setSession(e.target.value)}
                    options={sessionOptions}
                    className="w-full" />
          </div>
          <div className="">
            <Select label="Sort By"
                    tabIndex="3"
                    value={sort}
                    onChange={(e) => setSort(e.target.value)}
                    highlight={false}
                    options={sortOptions}
                    className="w-full" />
          </div>
        </div>

        <div className="m-4">
          <Accordion title={advancedSearchTitleEls}>
            <div className="m-5">
              <div className="flex justify-between flex-wrap">
                <div className={advancedFilterColumnClass}>
                  {advancedSelectEls()}
                </div>
                <div className={advancedFilterColumnClass}>
                  {advancedInputEls().slice(0, 5)}
                </div>
                <div className={advancedFilterColumnClass}>
                  {advancedInputEls().slice(5, 10)}
                </div>
                <div className={advancedFilterColumnClass}>
                  {advancedCheckboxEls()}
                </div>
              </div>
              <div className="m-3">
                <p className="text text--small">
                  Tip: To match an entire phrase, surround your query with double quotes.
                </p>
              </div>
              {Object.entries(refine).some(([ key, value ]) => value.value) &&
                <div className="m-3">
              <span onClick={resetFilters}
                    className="text-blue-500 font-medium cursor-pointer">Reset Advanced Filters</span>
                </div>
              }
            </div>
          </Accordion>
        </div>

        <div className="flex justify-end">
          <button className="btn btn--primary my-3 w-36" type="submit" tabIndex="4">Search</button>
        </div>
      </form>
    </div>
  )
}

function SearchCheckbox({ label, value, onChange }) {
  let className = "label cursor-pointer m-2 p-2"
  className += value ? " bg-yellow-100" : ""
  return (
    <React.Fragment>
      <input id={label}
             name={label}
             onChange={onChange}
             checked={value}
             type="checkbox"
             className="cursor-pointer"
      />
      <label htmlFor={label} className={className}>{label}</label>
    </React.Fragment>
  )
}
