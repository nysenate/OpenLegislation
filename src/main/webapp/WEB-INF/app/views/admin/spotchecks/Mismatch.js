import React from "react"
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { fetchMismatchHtmlDiff } from "app/apis/spotcheckApi";
import DataSourceLink from "app/views/admin/spotchecks/DataSourceLink";
import Select, { SelectOption } from "app/shared/Select";
import { Checkbox } from "app/shared/Checkbox";


export default function Mismatch({ mismatchId, mismatchTypeToDisplayName }) {
  const [ mismatch, setMismatch ] = React.useState()
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ whitespaceOption, setWhitespaceOption ] = React.useState(whitespaceOptions[0].value)
  const [ shouldUseAllCaps, setShouldUseAllCaps ] = React.useState(false)
  const [ shouldRemoveLineNums, setShouldRemoveLineNums ] = React.useState(false)

  const apiSearchParams = () => {
    let charOpts = []
    if (shouldUseAllCaps) {
      charOpts.push("ALL_CAPS")
    }
    if (shouldRemoveLineNums) {
      charOpts.push("REMOVE_LINE_NUMBERS")
    }

    return {
      whitespaceOption: whitespaceOption,
      characterOptions: charOpts
    }
  }

  React.useEffect(() => {
    setIsLoading(true)
    // Fetch a mismatch with html diffs included.
    fetchMismatchHtmlDiff(mismatchId, apiSearchParams())
      .then(res => setMismatch(res.result))
      .finally(() => setIsLoading(false))
  }, [ mismatchId, whitespaceOption, shouldUseAllCaps, shouldRemoveLineNums ])

  if (!mismatch && isLoading) {
    return (
      <LoadingIndicator />
    )
  }

  return (
    <div>
      <div className="flex justify-between">
        <div>
          <ul>
            <li>Last Reported: {formatDateTime(mismatch.observedDateTime, DateTime.DATETIME_SHORT)}</li>
            {mismatchContentFields(mismatch)}
            <li>Error Type: {mismatchTypeToDisplayName.get(mismatch.mismatchType)}</li>
          </ul>
        </div>
        <div>
          <Select label=""
                  value={whitespaceOption}
                  options={whitespaceOptions}
                  onChange={e => setWhitespaceOption(e.target.value)}
                  name="whitespace select" />
          <div className="mt-2 mb-1">
            <Checkbox label="All Caps"
                      value={shouldUseAllCaps}
                      onChange={e => setShouldUseAllCaps(e.target.checked)} />
          </div>
          <Checkbox label="Remove Line Numbers"
                    value={shouldRemoveLineNums}
                    onChange={e => setShouldRemoveLineNums(e.target.checked)} />
        </div>
      </div>
      {isLoading &&
        <div className="mt-6">
          <LoadingIndicator />
        </div>
      }
      {!isLoading &&
        <div className="flex between space-x-16 mt-6">
          <div className="w-6/12 border-1 border-blue-800 bg-gray-100">
            <div className="bg-blue-800 text-white text-center py-3">
              <DataSourceLink datasource={mismatch.dataSource}
                              contentType={mismatch.contentType}
                              contentKey={mismatch.key} />
            </div>
            <div className="mx-3">
            <pre className="whitespace-pre font-mono text-sm overflow-auto h-[calc(55vh-10px)]"
                 dangerouslySetInnerHTML={{ __html: mismatch.referenceHtmlDiff }} />
            </div>
          </div>

          <div className="w-6/12 border-1 border-blue-800 bg-gray-100">
            <div className="bg-blue-800 text-white text-center py-3">
              <DataSourceLink datasource="OPENLEGLOCAL"
                              contentType={mismatch.contentType}
                              contentKey={mismatch.key} />
            </div>
            <div className="mx-3">
            <div className="whitespace-pre font-mono text-sm overflow-auto h-[calc(55vh-10px)]"
                 dangerouslySetInnerHTML={{ __html: mismatch.observedHtmlDiff }} />
            </div>
          </div>
        </div>
      }
    </div>
  )
}

const whitespaceOptions = [
  new SelectOption("NONE", "No Formatting"),
  new SelectOption("NORMALIZE_WHITESPACE", "Normalize Whitespace"),
  new SelectOption("REMOVE_WHITESPACE", "Remove All Whitespace"),
]

const mismatchContentFields = mismatch => {
  switch (mismatch.contentType) {
    case "BILL":
    case "BILL_AMENDMENT":
      return <>
        <li>Session: {mismatch.key.session.year}</li>
        <li>Print No: {mismatch.key.printNo}</li>
      </>
    case "CALENDAR":
      return <>
        <li>Year: {mismatch.key.year}</li>
        <li>Cal No: {mismatch.key.calNo}</li>
        <li>Type: {mismatch.key.type}</li>
      </>
    case "AGENDA":
      return <>
        <li>Year: {mismatch.key.agendaId.year}</li>
        <li>Agenda No: {mismatch.key.agendaId.number}</li>
        <li>Committee: {mismatch.key.committeeId.name}</li>
      </>
    case "AGENDA_WEEK":
      return <>
        <li>Week of: {mismatch.key.weekOf}</li>
        <li>Committee: {mismatch.key.committeeId.name}</li>
      </>
    case "LAW":
      if (mismatch.mismatchType === "LAW_IDS") {
        return <li>Chapter: All</li>
      }
      return <>
        <li>Chapter: {mismatch.key.lawChapter}</li>
        <li>Loc Id: {mismatch.key.locationId}</li>
      </>
  }
}
