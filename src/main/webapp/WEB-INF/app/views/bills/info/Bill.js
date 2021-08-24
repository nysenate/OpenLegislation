import React, { useEffect } from 'react';
import { getBillApi } from "app/apis/billGetApi";
import { sessionYear } from "app/lib/dateUtils";
import {
  useRouteMatch
} from "react-router-dom";
import BillSummary from "app/views/bills/info/BillSummary";
import BillInfo from "app/views/bills/info/BillInfo";

export default function Bill({ setHeaderText }) {

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
  }, [match])

  if (loading) {
    return (<div>Loading ...</div>)
  }

  return (
    <div>
      <BillSummary bill={bill} />
      <BillInfo bill={bill} />
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
