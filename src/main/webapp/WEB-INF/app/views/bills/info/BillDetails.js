import React from 'react'
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";

export default function BillDetails({ bill }) {
  const [ version, setVersion ] = React.useState(bill.activeVersion)
  const [ activeTab, setActiveTab ] = React.useState("Details")

  return (
    <div>
      <div className="px-5">
        <AmendmentSwitcher bill={bill} version={version} setVersion={setVersion} />
      </div>

      <div className="mb-5">
        <Tabs tabs={billInfoTabs(bill, version)} activeTab={activeTab} setActiveTab={setActiveTab}>
        </Tabs>
      </div>
      {activeTab}
    </div>
  )
}

const billInfoTabs = (bill, version) => {
  return [
    {
      name: "Details",
      quantity: undefined,
      isDisabled: false,
    },
    {
      name: "Votes",
      quantity: bill.votes.size,
      isDisabled: bill.votes.size === 0,
    },
    {
      name: "Memos",
      quantity: (bill.amendments.items[version].memo ? 1 : 0) + bill.vetoMessages.size + (bill.approvalMessage ? 1 : 0),
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

function AmendmentSwitcher({ bill, version, setVersion }) {
  if (bill?.amendments?.size <= 1) {
    return null
  }

  return (
    <React.Fragment>
      <div>
        <label className="flex items-center">
          <h4 className="h5 my-3 mr-3">Amendment Version</h4>
          <select value={version} onChange={(e) => {
            setVersion(e.target.value)
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

function Tabs({ tabs, activeTab, setActiveTab }) {
  return (
    <div className="flex mt-5 pl-5 border-b-1 border-blue-600">
      {tabs.map((tab, index) => {
        return (
          <Tab key={tab.name}
               tab={tab}
               isActive={tab.name === activeTab}
               setActiveTab={setActiveTab} />
        )
      })}
    </div>
  )
}

function Tab({ tab, isActive, setActiveTab }) {
  let tabClass = "px-3 py-1 mr-3 whitespace-nowrap border-t-1 border-l-1 border-r-1"

  if (tab.isDisabled) {
    tabClass += " cursor-default bg-gray-50 text-gray-400 font-extralight border-gray-50"
  } else if (isActive) {
    tabClass += " text-blue-600 font-semibold bg-white border-blue-600 -mb-px"
  } else {
    tabClass += " text-gray-500 font-light cursor-pointer bg-gray-100"
  }

  return (
    <div className={tabClass}
         onClick={tab.isDisabled ? undefined : () => setActiveTab(tab.name)}>
      {tab.name}{tab.quantity ? ` (${tab.quantity})` : ""}
    </div>
  )
}
