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
  const [ session, setSession ] = React.useState(sessionOptions()[0].value)
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
    setSession(params.session || sessionOptions()[0].value)
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
        return (
          <div className={filterWrapperClass} key={key}>
            <SearchSelect label={value.label}
                          value={value.value}
                          onChange={(e) => dispatch({
                            type: "update",
                            value: e.target.value,
                            key: key
                          })}
                          options={value.options} />
          </div>
        )
      })
  }

  const advancedInputEls = () => {
    return Object.entries(refine).filter(([ key, value ]) => {
        return value.type === "input"
      })
      .map(([ key, value ]) => {
        return (
          <div className={filterWrapperClass} key={key}>
            <SearchTextInput label={value.label}
                             value={value.value}
                             onChange={(e) => dispatch({
                               type: "update",
                               value: e.target.value,
                               key: key
                             })}
                             placeholder={value.placeholder} />
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
            <SearchSelect label="Session Year"
                          tabindex={2}
                          value={session}
                          onChange={(e) => setSession(e.target.value)}
                          highlight={false}
                          options={sessionOptions()} />
          </div>
          <div className="">
            <SearchSelect label="Sort By"
                          tabindex={3}
                          value={sort}
                          onChange={(e) => setSort(e.target.value)}
                          highlight={false}
                          options={sortOptions} />
          </div>
        </div>

        <div className="m-4">
          <Accordion title={advancedSearchTitleEls}>
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
          </Accordion>
        </div>

        <div className="flex justify-end">
          <button className="btn my-3 w-36" type="submit" tabIndex="4">Search</button>
        </div>
      </form>
    </div>
  )
}

function SearchSelect({ label, value, onChange, options, tabindex, highlight = true }) {
  let className = "label label--top"
  if (highlight) {
    // If highlight and value are truthy, highlight the label.
    className += value ? " bg-yellow-100" : ""
  }
  return (
    <label className={className}>{label}
      <select value={value}
              tabIndex={tabindex}
              onChange={onChange}
              className="select w-full">
        {options && options.map((opt) => <option value={opt.value} key={opt.value}>{opt.label}</option>)}
      </select>
    </label>
  )
}

function SearchTextInput({ label, value, onChange, placeholder }) {
  let className = "label label--top"
  className += value ? "bg-yellow-100" : ""
  return (
    <div>
      <label className={className}>{label}
        <input value={value}
               onChange={onChange}
               type="text"
               placeholder={placeholder}
               className="input w-full"
        />
      </label>
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
