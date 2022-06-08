import React from 'react'
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import { Checkbox } from "app/shared/Checkbox";
import BillListing from "app/shared/BillListing";
import Input from "app/shared/Input";
import { useDebounce } from "use-debounce";
import Pagination, { PageParams } from "app/shared/Pagination";


const INIT_PAGE_PARAMS = new PageParams(1, 8)

/**
 * Returns an 2 element array where the first element is an array of bills matching all the given filters
 * and the second element is the count of bills matching the `checkboxes` and `filter` filters (to be used
 * as the total count of results in pagination).
 * @param suppMap {Map} A Map of all supplementals in this active list.
 * @param checkboxes {Map} A Map containing the status of the supplemental checkbox filters.
 * @param filter {string} Text to filter for.
 * @param pageParams {PageParams} Page information used to return only the bills that belong on the current page.
 */
const filterBills = (suppMap, checkboxes, filter, pageParams) => {
  let bills = [ ...checkboxes.values() ]
    .filter(checkbox => checkbox.isChecked) // Get all selected supplementals.
    .flatMap(c => suppMap.get(c.key).entries.items) // Combine list of bills for each selected supplemental.
  if (filter) {
    // Filter for search term.
    bills = bills.filter(b => {
      return b.printNo.includes(filter) || b.summary.includes(filter) || b.title.includes(filter)
    })
  }
  const total = bills.length
  bills = bills.slice(pageParams.offset - 1, pageParams.limit) // Get only the bills for the current page.
  return [ bills, total ]
}

const activeListReducer = function (state, action) {
  switch (action.type) {
    case "init": {
      const suppMap = new Map(Object.keys(action.payload.items).map(key => [ key, action.payload.items[key] ]))
      const initCheckboxes = createCheckboxes(suppMap)
      const initFilter = ""
      const [ initBills, total ] = filterBills(suppMap, initCheckboxes, initFilter, INIT_PAGE_PARAMS)
      return {
        ...state,
        supplementalMap: suppMap,
        checkboxes: initCheckboxes,
        pageParams: INIT_PAGE_PARAMS,
        bills: initBills,
        total: total,
        filter: initFilter,
      }
    }
    case "onCheckboxChange": {
      let newCheckboxes = new Map(state.checkboxes)
      newCheckboxes.get(action.payload.key).isChecked = action.payload.checked
      const [ bills, total ] = filterBills(state.supplementalMap, newCheckboxes, state.filter, INIT_PAGE_PARAMS)
      return {
        ...state,
        checkboxes: newCheckboxes,
        pageParams: INIT_PAGE_PARAMS, // Reset to page 1.
        bills: bills,
        total: total,
      }
    }
    case "onFilterChange": {
      return {
        ...state,
        filter: action.payload,
      }
    }
    case "onDebounce": {
      const pageParms = INIT_PAGE_PARAMS
      const [ bills, total ] = filterBills(state.supplementalMap, state.checkboxes, state.filter, pageParms)
      return {
        ...state,
        bills: bills,
        total: total,
        pageParams: pageParms,
      }
    }
    case "onPageChange": {
      const pageParams = action.payload
      const [ bills, total ] = filterBills(state.supplementalMap, state.checkboxes, state.filter, pageParams)
      return {
        ...state,
        pageParams: pageParams,
        bills: bills,
        total: total,
      }
    }
    default:
      return state
  }
}

const initState = {
  supplementalMap: new Map(),
  checkboxes: new Map(),
  pageParams: INIT_PAGE_PARAMS,
  bills: [],
  total: 0, // Total count of bills matching selected supplementals and text filter.
  filter: "",
}

export default function ActiveList({ activeList }) {
  const [ state, dispatch ] = React.useReducer(activeListReducer, initState)
  const [ debounceFilter ] = useDebounce(state.filter, 200)

  React.useEffect(() => {
    dispatch({ type: "init", payload: activeList })
  }, [ activeList ])

  React.useEffect(() => {
    dispatch({ type: "onDebounce" })
  }, [ debounceFilter ])

  return (
    <div className="m-3">
      <SupplementalCheckboxes state={state} dispatch={dispatch} />
      <hr />
      <div className="my-3">
        <Input label="Filter Bills"
               value={state.filter}
               onChange={e => dispatch({ type: "onFilterChange", payload: e.target.value })}
               placeholder="e.g. S100" />
      </div>
      <Pagination currentPage={state.pageParams.selectedPage}
                  limit={state.pageParams.limit}
                  total={state.total}
                  onPageChange={pageInfo => dispatch({ type: "onPageChange", payload: pageInfo })} />
      {state.bills.map(b =>
        <BillListing bill={b} to={`/bills/${b.session}/${b.basePrintNo}`} key={b.basePrintNo} />
      )}
      <Pagination currentPage={state.pageParams.selectedPage}
                  limit={state.pageParams.limit}
                  total={state.total}
                  onPageChange={pageInfo => dispatch({ type: "onPageChange", payload: pageInfo })} />
    </div>
  )
}

function SupplementalCheckboxes({ state, dispatch }) {
  return (
    <div className="my-6 flex items-center gap-x-6 flex-wrap gap-y-3">
      <span className="h5">Supplementals: </span>
      {[ ...state.checkboxes.keys() ].map((key, i) => {
        const box = state.checkboxes.get(key)
        return (
          <span key={i}>
            <Checkbox label={box.label}
                      value={box.isChecked}
                      onChange={e => dispatch({
                        type: "onCheckboxChange",
                        payload: { key: key, checked: e.target.checked }
                      })}
                      name={key} />
          </span>)
      })}
    </div>
  )
}

/**
 * Stores checkbox related information for a single supplemental.
 * @param key {string} The id/key for the supplemental this checkbox represents.
 * @param isChecked {boolean} Is this checkbox checked.
 * @param label {string|jsx} A string or component to use as a label for this checkbox.
 */
function SupplementalCheckbox(key, isChecked, label) {
  this.key = key
  this.isChecked = isChecked
  this.label = label
}

/**
 * Creates a SupplementalCheckbox for each supplemental.
 * @param suppMap
 * @returns {Map<any, any>} Map of supplemental number to SupplementalCheckbox.
 */
const createCheckboxes = suppMap => {
  let boxes = new Map()
  for (const key of suppMap.keys()) {
    boxes.set(key, new SupplementalCheckbox(key, true, createCheckboxLabel(key, suppMap.get(key))))
  }
  return boxes
}

const createCheckboxLabel = (key, supplemental) => {
  let label = ""
  switch (key) {
    case "0":
      label = "Original"
      break
    default:
      label = `Supplemental ${key}`
      break
  }
  return (
    <div>
      <h5 className="h5">{label}</h5>
      <span className="text text--small">{formatDateTime(supplemental.releaseDateTime, DateTime.DATETIME_MED)}</span>
    </div>
  )
}