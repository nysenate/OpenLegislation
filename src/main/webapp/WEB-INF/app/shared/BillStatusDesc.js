import React from "react"

export default function BillStatusDesc({ status, className }) {
  if (!status) {
    return null
  }

  let desc = ""
  switch (status.statusType) {
    case "IN_SENATE_COMM":
      desc = "In Senate " + status.committeeName + " Committee";
      break;
    case "IN_ASSEMBLY_COMM":
      desc = "In Assembly " + status.committeeName + " Committee";
      break;
    case "SENATE_FLOOR":
      desc = "On Senate Floor as Calendar No: " + status.billCalNo;
      break;
    case "ASSEMBLY_FLOOR":
      desc = "On Assembly Floor as Calendar No: " + status.billCalNo;
      break;
    default:
      desc = status.statusDesc;
  }

  return (
    <span className={`${className}`}>{desc}</span>
  )
}