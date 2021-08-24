import React from 'react'
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";
import Tabs from "app/shared/Tabs";
import BillSummaryTab from "app/views/bills/info/BillDetailsTab";

export default function BillInfo({ bill }) {
  const [ selectedAmd, setSelectedAmd ] = React.useState(bill.activeVersion)
  const [ activeTab, setActiveTab ] = React.useState("Summary")
  const [ tabs, setTabs ] = React.useState([])

  React.useEffect(() => {
    setTabs(billInfoTabs(bill, selectedAmd))
  }, [bill, selectedAmd])

  return (
    <div>
      <div className="px-5">
        <AmendmentSwitcher bill={bill} selectedAmd={selectedAmd} setSelectedAmd={setSelectedAmd} />
      </div>

      <div className="mb-5">
        <Tabs tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab} />
      </div>
    </div>
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
