import React, { useEffect } from 'react';
import { getBillApi } from "app/apis/billGetApi";
import { sessionYear } from "app/lib/dateUtils";
import {
  useRouteMatch
} from "react-router-dom";
import BillSummary from "app/views/bills/info/BillSummary";
import BillDetails from "app/views/bills/info/BillDetails";

export default function BillInfo({ setHeaderText }) {

  const [ bill, setBill ] = React.useState({})
  const [ loading, setLoading ] = React.useState(true)
  const match = useRouteMatch()

  useEffect(() => {
    getBillApi(match.params.sessionYear, match.params.printNo, "with_refs_no_fulltext")
      .then((bill) => {
        setBill(bill)
        setHeaderText(headerTextForBill(bill))
        setLoading(false)
      })
  }, [])

  if (loading) {
    return (<div>Loading ...</div>)
  }

  return (
    <div className="p-5">
      <BillSummary bill={bill} />
      <BillDetails bill={bill} />
    </div>
  )
}

function headerTextForBill(bill) {
  if (!bill.billType) {
    return ""
  }
  const type = bill.billType.resolution ? "Resolution" : "Bill"
  const isActiveText = bill.session === sessionYear() ? "" : "(Inactive)"
  return `NYS ${bill.billType.desc} ${type} ${bill.printNo}-${bill.session} ${isActiveText}`
}
