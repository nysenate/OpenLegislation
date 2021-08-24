import React from "react";
import { getBillApi } from "app/apis/billGetApi";
import BillListing from "app/shared/BillListing";

/**
 * Loads the bill info for the given bill and displays a BillListing for it.
 * @param bill A non full bill view, i.e. BillIdView
 */
export default function BillListingQueried({ bill }) {
  const [ sameAsBill, setSameAsBill ] = React.useState()

  React.useEffect(() => {
    getBillApi(bill.session, bill.printNo, "info")
      .then((bill) => {
        setSameAsBill(bill)
      })
  }, [ bill ])

  return (
    <React.Fragment>
      <BillListing bill={sameAsBill} to={`/bills/${bill.session}/${bill.basePrintNo}`} />
    </React.Fragment>
  )
}