import React from 'react';
import { getBillApi } from "app/apis/billGetApi";
import {
  formatDateTime,
  sessionYear
} from "app/lib/dateUtils";
import {
  Link,
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import {
  FileDotted,
} from "phosphor-react";
import BillOverview from "app/views/bills/info/BillOverview";
import { DateTime } from "luxon";
import Tabs from "app/shared/Tabs";
import BillSummaryTab from "app/views/bills/info/BillSummaryTab";
import BillSponsorsTab from "app/views/bills/info/BillSponsorsTab";
import * as queryString from "query-string";
import BillMemosTab from "app/views/bills/info/BillMemosTab";
import BillFullTextTab from "app/views/bills/info/BillFullTextTab";
import BillVotesTab from "app/views/bills/info/BillVotesTab";
import BillActionsTab from "app/views/bills/info/BillActionsTab";

export default function Bill({ setHeaderText }) {

  const [ loading, setLoading ] = React.useState(true)
  const [ bill, setBill ] = React.useState()
  const [ selectedAmd, setSelectedAmd ] = React.useState()
  const [ tabs, setTabs ] = React.useState([])
  const [ activeTab, setActiveTab ] = React.useState()
  const match = useRouteMatch()
  const location = useLocation()
  const history = useHistory()

  // Initialize data when a bill page is navigated to
  React.useEffect(() => {
    getBillApi(match.params.sessionYear, match.params.printNo, { view: "with_refs_no_fulltext" })
      .then((bill) => {
        setBill(bill)
        setStateFromSearchParams("Summary", bill.activeVersion)
        setHeaderText(headerTextForBill(bill))
        setLoading(false)
      })
  }, [ match.params.sessionYear, match.params.printNo ])

  // Update tab labels and content whenever bill or selected amd change.
  React.useEffect(() => {
    if (bill && (selectedAmd != null)) {
      setTabs(billInfoTabs(bill, selectedAmd))
    }
  }, [ bill, selectedAmd ])

  // Update selectedAmd and active tab on back/forward navigation.
  React.useEffect(() => {
    if (bill) {
      setStateFromSearchParams("Summary", bill.activeVersion)
    }
  }, [ location.search ])

  const setStateFromSearchParams = (defaultTab, defaultAmd) => {
    const params = queryString.parse(location.search, { parseBooleans: true })
    setSelectedAmd(params.amendment == null ? defaultAmd : params.amendment)
    setActiveTab(params.tab || defaultTab)
  }

  const updateSearchParams = (searchParams) => {
    const params = queryString.parse(location.search)
    params.tab = searchParams.tab
    params.amendment = searchParams.amd
    history.push({ search: queryString.stringify(params) })
  }

  const onTabChange = (tab) => {
    setActiveTab(tab)
    updateSearchParams({ amd: selectedAmd, tab: tab })
  }

  const onAmdChange = (amd) => {
    setSelectedAmd(amd)
    updateSearchParams({ amd: amd, tab: activeTab })
  }

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
        <AmendmentSwitcher bill={bill} selectedAmd={selectedAmd} setSelectedAmd={onAmdChange} />
      </div>
      <div className="mb-5">
        <Tabs tabs={tabs} activeTab={activeTab} setActiveTab={onTabChange} />
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
          <select value={selectedAmd} onChange={(e) => setSelectedAmd(e.target.value)} className="select m-3">
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
      name: "Sponsors",
      quantity: undefined,
      isDisabled: (bill.additionalSponsors.size
        + bill.amendments.items[selectedAmd].coSponsors.size
        + bill.amendments.items[selectedAmd].multiSponsors.size) === 0,
      component: <BillSponsorsTab bill={bill} selectedAmd={selectedAmd} />
    },
    {
      name: "Votes",
      quantity: bill.votes.size,
      isDisabled: bill.votes.size === 0,
      component: <BillVotesTab bill={bill} selectedAmd={selectedAmd} />
    },
    {
      name: "Memos",
      quantity: (bill.amendments.items[selectedAmd].memo ? 1 : 0) + bill.vetoMessages.size + (bill.approvalMessage ? 1 : 0),
      isDisabled: bill.billType.resolution,
      component: <BillMemosTab bill={bill} selectedAmd={selectedAmd} />
    },
    {
      name: "Actions",
      quantity: bill.actions.size,
      isDisabled: bill.actions.size === 0,
      component: <BillActionsTab bill={bill} />
    },
    {
      name: "Full Text",
      quantity: undefined,
      isDisabled: false,
      component: <BillFullTextTab bill={bill} selectedAmd={selectedAmd} />
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
