import React, { useEffect } from 'react';
import { getBillApi } from "app/apis/billGetApi";
import { sessionYear } from "app/lib/dateUtils";
import {
  Link,
  useRouteMatch
} from "react-router-dom";
import MemberThumbnail from "app/shared/MemberThumbnail";
import {
  Check,
  CheckCircle,
  FileDotted,
  XCircle
} from "phosphor-react";
import FullDate from "app/shared/FullDate";
import BillStatusDesc from "app/shared/BillStatusDesc";
import BillMilestones from "app/shared/BillMilestones";
import BillSummary from "app/views/bills/info/BillSummary";

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
    <BillSummary bill={bill} />
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
