import React from 'react'
import BillListing from "app/shared/BillListing";
import Input from "app/shared/Input";
import { useDebounce } from "use-debounce";
import Pagination, { PageParams } from "app/shared/Pagination";
import SupplementalCheckboxes, { createCheckboxes } from "app/views/calendars/SupplementalCheckbox";


const reducer = function (state, action) {
  switch (action.type) {
    case "init": {
      const initCheckboxes = createCheckboxes(action.payload)
      const initFilter = ""
      const [ initBills, total ] = filterBills(action.payload, initCheckboxes, initFilter, initState.pageParams)
      return {
        ...state,
        sectionMap: action.payload,
        checkboxes: initCheckboxes,
        pageParams: initState.pageParams,
        bills: initBills,
        total: total,
        filter: initFilter,
      }
    }
    case "onCheckboxChange": {
      let newCheckboxes = new Map(state.checkboxes)
      newCheckboxes.get(action.payload.key).isChecked = action.payload.checked
      const [ bills, total ] = filterBills(state.sectionMap, newCheckboxes, state.filter, initState.pageParams)
      return {
        ...state,
        checkboxes: newCheckboxes,
        pageParams: initState.pageParams, // Reset to page 1.
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
      const pageParms = initState.pageParams
      const [ bills, total ] = filterBills(state.sectionMap, state.checkboxes, state.filter, pageParms)
      return {
        ...state,
        bills: bills,
        total: total,
        pageParams: pageParms,
      }
    }
    case "onPageChange": {
      const pageParams = action.payload
      const [ bills, total ] = filterBills(state.sectionMap, state.checkboxes, state.filter, pageParams)
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
  sectionMap: new Map(),
  checkboxes: new Map(),
  pageParams: new PageParams(1, 6),
  bills: [],
  total: 0, // Total count of bills matching selected supplementals and text filter.
  filter: "",
}

export default function CalendarSectionList({ section }) {
  const [ state, dispatch ] = React.useReducer(reducer, initState)
  const [ debounceFilter ] = useDebounce(state.filter, 200)

  React.useEffect(() => {
    dispatch({ type: "init", payload: section })
  }, [ section ])

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

/**
 * Returns an 2 element array where the first element is an array of bills matching all the given filters
 * and the second element is the count of bills matching the `checkboxes` and `filter` filters (to be used
 * as the total count of results in pagination).
 * @param suppMap {Map} A Map of all supplementals in this calendar section.
 * @param checkboxes {Map} A Map containing the status of the supplemental checkbox filters.
 * @param filter {string} Text to filter for.
 * @param pageParams {PageParams} Page information used to return only the bills that belong on the current page.
 */
const filterBills = (suppMap, checkboxes, filter, pageParams) => {
  let bills = [ ...checkboxes.values() ]
    .filter(checkbox => checkbox.isChecked) // Get all selected supplementals.
    .flatMap(c => suppMap.supplementals.get(c.key).entries) // Combine list of bills for each selected supplemental.
  if (filter) {
    // Filter for search term.
    bills = bills.filter(b => {
      return b.printNo.includes(filter) || b.summary.includes(filter) || b.title.includes(filter)
    })
  }
  const total = bills.length
  // Get only the bills for the current page.
  bills = bills.slice(pageParams.offset - 1, pageParams.offset - 1 + pageParams.limit)
  return [ bills, total ]
}
