import React from "react";
import { getBillApi } from "app/apis/billGetApi";
import BillListing, { BillInfoListing } from "app/shared/BillListing";


const initialState = {
  bill: {},
  isLoading: true,
  isError: false,
}

// action types that can be dispatched.
const ACTIONS = {
  API_PRE: "api-pre",
  API_SUCCESS: "api-success",
  API_ERROR: "api-error",
}

const reducer = function (state, action) {
  switch (action.type) {
    case ACTIONS.API_PRE: {
      return {
        ...state,
        isLoading: true,
        isError: false,
        bill: {},
      }
    }
    case ACTIONS.API_SUCCESS: {
      return {
        ...state,
        isLoading: false,
        bill: action.payload.result,
      }
    }
    case ACTIONS.API_ERROR: {
      return {
        ...state,
        isLoading: false,
        isError: true,
      }
    }
    default: {
      return state
    }
  }
}

/**
 * Attempts to load the full bill for the given bill info.
 * If successful, a BillListing is returned, if the bill is not found, a BillInfoListing is returned.
 * @param billInfo A BillIdView
 */
export default function BillListingQueried({ billInfo }) {
  const [ state, dispatch ] = React.useReducer(reducer, initialState)

  React.useEffect(() => {
    dispatch({ type: ACTIONS.API_PRE })
    getBillApi(billInfo.session, billInfo.printNo, { view: "info" })
      .then((res) => dispatch({ type: ACTIONS.API_SUCCESS, payload: res }))
      .catch((err) => dispatch({ type: ACTIONS.API_ERROR, payload: err }))
  }, [ billInfo ])

  if (state.isLoading) {
    return null
  }

  if (state.isError) {
    return (
      <React.Fragment>
        <BillInfoListing billInfo={billInfo} />
      </React.Fragment>
    )
  }

  return (
    <React.Fragment>
      <BillListing bill={state.bill} to={`/bills/${state.bill.session}/${state.bill.basePrintNo}`} />
    </React.Fragment>
  )
}
