import React from 'react';
import { getBillApi } from "app/apis/billGetApi";
import {
  formatDateTime,
  sessionYear
} from "app/lib/dateUtils";
import {
  Link,
  useRouteMatch
} from "react-router-dom";
import {
  FileDotted,
} from "phosphor-react";
import BillOverview from "app/views/bills/info/BillOverview";
import { DateTime } from "luxon";
import Tabs from "app/shared/Tabs";
import BillSummaryTab from "app/views/bills/info/BillDetailsTab";

export default function Bill({ setHeaderText }) {

  const [ loading, setLoading ] = React.useState(true)
  const [ bill, setBill ] = React.useState({})
  const [ selectedAmd, setSelectedAmd ] = React.useState()
  const [ tabs, setTabs ] = React.useState([])
  const [ activeTab, setActiveTab ] = React.useState("Summary")
  const match = useRouteMatch()

  React.useEffect(() => {
    if (bill && selectedAmd) {
      setTabs(billInfoTabs(bill, selectedAmd))
    }
  }, [ bill, selectedAmd ])

  React.useEffect(() => {
    getBillApi(match.params.sessionYear, match.params.printNo, "with_refs_no_fulltext")
      .then((bill) => {
        setBill(bill)
        setSelectedAmd(bill.activeVersion)
        setHeaderText(headerTextForBill(bill))
        setLoading(false)
      })
  }, [ match ])

  if (loading) {
    return (<div>Loading ...</div>)
  }

  return (
    <div>
      <div className="mx-8">
        <SubstitutedByMsg bill={bill} />
        <h3 className="h3">{bill.title}</h3>
        <ProgramInfoMsg bill={bill} />
        <BillOverview bill={bill} />
        <AmendmentSwitcher bill={bill} selectedAmd={selectedAmd} setSelectedAmd={setSelectedAmd} />
      </div>
      <div className="mb-5">
        <Tabs tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab} />
      </div>
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

function SubstitutedByMsg({ bill }) {
  if (!bill.substitutedBy) {
    return null
  }
  return (
    <div className="bg-yellow-50 py-2 mb-2 flex items-center rounded">
      <FileDotted size="1.2rem" className="mx-1" />
      <p>
        This bill has been substituted by&nbsp;
        <Link to={`/bills/${bill.substitutedBy.session}/${bill.substitutedBy.basePrintNo}`}>
          {bill.substitutedBy.basePrintNo} - {bill.substitutedBy.session}
        </Link>
      </p>
    </div>
  )
}

function ProgramInfoMsg({ bill }) {
  if (!bill.programInfo) {
    return null
  }
  return (
    <div className="pb-3 text">
      Bill #{bill.programInfo.sequenceNo} on the program
      for <span className="font-medium">{bill.programInfo.name}</span>
    </div>
  )
}

function AmendmentSwitcher({ bill, selectedAmd, setSelectedAmd }) {
  if (bill?.amendments?.size <= 1) {
    return null
  }

  return (
    <React.Fragment>
      <div>
        <label className="flex items-center">
          <h4 className="h5 my-3 mr-3">Amendment Version</h4>
          <select value={selectedAmd} onChange={(e) => {
            setSelectedAmd(e.target.value)
          }} className="select m-3">
            {Object.entries(bill.amendments.items).map(([ key, amd ]) => {
              let label = amd.version === "" ? "Original" : `Revision ${amd.version}`
              if (amd.version === bill.activeVersion) {
                label += " (Latest)"
              }
              return (
                <option value={amd.version} key={key}>
                  {label} - {formatDateTime(amd.publishDate, DateTime.DATE_MED)}
                </option>
              )
            })}
          </select>
        </label>
      </div>
    </React.Fragment>
  )
}


const billInfoTabs = (bill, selectedAmd) => {
  return [
    {
      name: "Summary",
      quantity: undefined,
      isDisabled: false,
      component: <BillSummaryTab bill={bill} selectedAmd={selectedAmd} />
    },
    {
      name: "Votes",
      quantity: bill.votes.size,
      isDisabled: bill.votes.size === 0,
    },
    {
      name: "Memos",
      quantity: (bill.amendments.items[selectedAmd].memo ? 1 : 0) + bill.vetoMessages.size + (bill.approvalMessage ? 1 : 0),
      isDisabled: bill.billType.resolution,
    },
    {
      name: "Actions",
      quantity: bill.actions.size,
      isDisabled: false,
    },
    {
      name: "Full Text",
      quantity: undefined,
      isDisabled: false,
    },
    {
      name: "Updates",
      quantity: undefined,
      isDisabled: false,
    },
    {
      name: "JSON",
      quantity: undefined,
      isDisabled: false,
    },
  ]
}
