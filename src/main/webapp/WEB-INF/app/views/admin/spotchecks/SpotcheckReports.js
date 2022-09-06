import React from "react"
import {
  fetchContentTypeCounts,
  fetchMismatchTypes,
  fetchReferenceTypes
} from "app/apis/spotcheckApi";
import Select, { SelectOption } from "app/shared/Select";
import { DateTime } from "luxon";
import DatePicker from "app/shared/DatePicker";
import Tabs from "app/shared/Tabs";
import LoadingIndicator from "app/shared/LoadingIndicator";
import SpotcheckContentTab from "app/views/admin/spotchecks/SpotcheckContentTab";


/**
 * A wrapper around the main Spotcheck Report page. This component loads necessary static data
 * from the back end before rendering the main component, SpotcheckReportsContent.
 */
export default function SpotcheckReports({ setHeaderText }) {
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ mismatchTypeToDisplayName, setMismatchTypeToDisplayName ] = React.useState()
  const [ referenceTypesByName, setReferenceTypesByName ] = React.useState()

  React.useEffect(() => {
    setHeaderText("Spotcheck Reports")
    setIsLoading(true)
    const mismatchTypePromise = fetchMismatchTypes()
      .then(res => setMismatchTypeToDisplayName(new Map(res.result.items.map(mt => [ mt.name, mt.displayName ]))))
    const refTypePromise = fetchReferenceTypes()
      .then(res => setReferenceTypesByName(new Map(res.result.items.map(rt => [ rt.name, rt ]))))

    Promise.all([ mismatchTypePromise, refTypePromise ])
      .then(() => setIsLoading(false))
  }, [])

  if (isLoading) {
    return <LoadingIndicator />
  }

  return (
    <SpotcheckReportsContent mismatchTypeToDisplayName={mismatchTypeToDisplayName}
                             referenceTypesByName={referenceTypesByName} />
  )
}

/**
 * The core functionality for the SpotcheckReports page starts here.
 */
function SpotcheckReportsContent({ mismatchTypeToDisplayName, referenceTypesByName }) {
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ datasource, setDatasource ] = React.useState(DATA_SOURCES.LBDC.value)
  const [ date, setDate ] = React.useState(DateTime.now())
  const [ contentTypeCounts, setContentTypeCounts ] = React.useState()
  const [ tabs, setTabs ] = React.useState()
  const [ activeTab, setActiveTab ] = React.useState(DATA_SOURCES[datasource].contentTypes[0]);

  React.useEffect(() => {
    setIsLoading(true)
    fetchContentTypeCounts(datasource, date)
      .then(res => setContentTypeCounts(res.result.summary.items))
      .finally(() => setIsLoading(false))
  }, [ datasource, date ])

  React.useEffect(() => {
    setActiveTab(DATA_SOURCES[datasource].contentTypes[0])
  }, [ datasource ])

  React.useEffect(() => {
    setTabs(createTabs())
  }, [ contentTypeCounts ])

  const createTabs = () => {
    return DATA_SOURCES[datasource].contentTypes.map(t => {
      return {
        name: t,
        quantity: contentTypeCounts?.[t] || 0,
        isDisabled: false,
        component: <SpotcheckContentTab datasource={datasource}
                                        date={date}
                                        contentType={t}
                                        mismatchTypeToDisplayName={mismatchTypeToDisplayName}
                                        referenceTypesByName={referenceTypesByName} />
      }
    })
  }

  if (isLoading) {
    return <LoadingIndicator />
  }

  return (
    <div>
      <div className="p-3 flex gap-x-6">
        <Select label="Comparison"
                value={datasource}
                options={DATA_SOURCE_SELECT_OPTIONS}
                onChange={e => setDatasource(e.target.value)}
                name="datasource" />
        <DatePicker label="Date"
                    name="date"
                    date={date}
                    setDate={d => setDate(d)}
                    maxDate={DateTime.now()} />
      </div>
      <div className="my-3">
        <Tabs tabs={tabs} activeTab={activeTab} setActiveTab={(name) => setActiveTab(name)} showZeroQuantity={true} />
      </div>
    </div>
  )
}

const DATA_SOURCES = {
  LBDC: {
    value: "LBDC",
    label: "LBDC - Open Legislation",
    contentTypes: [ "BILL", "CALENDAR", "AGENDA_WEEK" ],
  },
  NYSENATE: {
    value: "NYSENATE",
    label: "NYSenate - Open Legislation",
    contentTypes: [ "BILL_AMENDMENT", "CALENDAR", "AGENDA", "LAW" ],
  },
  OPENLEG: {
    value: "OPENLEG",
    label: "OL Reference - OL Source",
    contentTypes: [ "BILL", "CALENDAR", "AGENDA" ],
  }
}

const DATA_SOURCE_SELECT_OPTIONS = [
  new SelectOption(DATA_SOURCES.LBDC.value, DATA_SOURCES.LBDC.label),
  new SelectOption(DATA_SOURCES.NYSENATE.value, DATA_SOURCES.NYSENATE.label),
  new SelectOption(DATA_SOURCES.OPENLEG.value, DATA_SOURCES.OPENLEG.label),
]
