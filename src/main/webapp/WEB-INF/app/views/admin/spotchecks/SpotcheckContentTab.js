import React from "react";
import {
  fetchMismatches,
  fetchMismatchStatusCounts,
  fetchMismatchTypeCounts
} from "app/apis/spotcheckApi";
import Select, { SelectOption } from "app/shared/Select";
import LoadingIndicator from "app/shared/LoadingIndicator";
import MismatchTable from "app/views/admin/spotchecks/MismatchTable";
import Pagination, { PageParams } from "app/shared/Pagination";


export default function SpotcheckContentTab({
                                              datasource,
                                              date,
                                              contentType,
                                              mismatchTypeToDisplayName,
                                              referenceTypesByName
                                            }) {
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ mismatchStatus, setMismatchStatus ] = React.useState("OPEN")
  const [ mismatchType, setMismatchType ] = React.useState("All")
  const [ mismatchResponse, setMismatchResponse ] = React.useState([])
  const [ pageParams, setPageParams ] = React.useState(new PageParams(1, 10))

  React.useEffect(() => {
    loadMismatches()
  }, [ datasource, date, contentType, mismatchStatus, mismatchType, pageParams ])

  const loadMismatches = () => {
    setIsLoading(true)
    fetchMismatches(datasource, date, contentType, mismatchStatus, mismatchType, pageParams)
      .then(res => setMismatchResponse(res))
      .finally(() => setIsLoading(false))
  }

  return (
    <div className="p-3">
      <div className="flex gap-x-6 mb-3">
        <MismatchStatusSelect mismatchStatus={mismatchStatus}
                              setMismatchStatus={setMismatchStatus}
                              datasource={datasource}
                              date={date}
                              contentType={contentType} />

        <MismatchTypeSelect mismatchType={mismatchType}
                            setMismatchType={setMismatchType}
                            datasource={datasource}
                            date={date}
                            contentType={contentType}
                            mismatchStatus={mismatchStatus}
                            mismatchTypeToDisplayName={mismatchTypeToDisplayName} />
      </div>
      {isLoading &&
        <LoadingIndicator />
      }
      {!isLoading &&
        <>
          <MismatchTable mismatches={mismatchResponse.result.items}
                         contentType={contentType}
                         mismatchTypeToDisplayName={mismatchTypeToDisplayName}
                         referenceTypesByName={referenceTypesByName}
                         reloadMismatches={() => loadMismatches()}
          />
          <Pagination currentPage={pageParams.selectedPage}
                      limit={pageParams.limit}
                      total={mismatchResponse.total}
                      onPageChange={page => setPageParams(page)} />
        </>
      }
    </div>
  )
}

function MismatchStatusSelect({ mismatchStatus, setMismatchStatus, datasource, date, contentType }) {
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ mismatchStatusCounts, setMismatchStatusCounts ] = React.useState()
  const [ mismatchStatusSelectOptions, setMismatchStatusSelectOptions ] = React.useState()

  React.useEffect(() => {
    setIsLoading(true)
    fetchMismatchStatusCounts(datasource, date, contentType)
      .then(res => setMismatchStatusCounts(res.result.summary.items))
      .finally(() => setIsLoading(false))
  }, [ datasource, date, contentType ])

  React.useEffect(() => {
    setMismatchStatusSelectOptions([
      new SelectOption("NEW", `New Issues (${mismatchStatusCounts?.NEW || 0})`),
      new SelectOption("OPEN", `Open Issues (${mismatchStatusCounts?.OPEN || 0})`),
      new SelectOption("RESOLVED", `Resolved Issues (${mismatchStatusCounts?.RESOLVED || 0})`),
    ])
  }, [ mismatchStatusCounts ])

  if (isLoading) {
    return <div className="w-36"><LoadingIndicator /></div>
  }

  return (
    <Select label="" value={mismatchStatus}
            options={mismatchStatusSelectOptions}
            onChange={e => setMismatchStatus(e.target.value)} />
  )
}

function MismatchTypeSelect({
                              mismatchType,
                              setMismatchType,
                              datasource,
                              date,
                              contentType,
                              mismatchStatus,
                              mismatchTypeToDisplayName
                            }) {
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ mismatchTypeSelectOptions, setMismatchTypeSelectOptions ] = React.useState()
  const [ mismatchTypeCounts, setMismatchTypeCounts ] = React.useState()

  React.useEffect(() => {
    setIsLoading(true)
    fetchMismatchTypeCounts(datasource, date, contentType, mismatchStatus)
      .then(res => setMismatchTypeCounts(res.result.typeCount.items))
      .finally(() => setIsLoading(false))
  }, [ datasource, date, contentType, mismatchStatus ])

  React.useEffect(() => {
    if (mismatchTypeCounts) {
      const selectOptions = Object.keys(mismatchTypeCounts)
        .map(t => new SelectOption(t, `${mismatchTypeToDisplayName.get(t)} (${mismatchTypeCounts[t]})`))
      setMismatchTypeSelectOptions(selectOptions)
    }
  }, [ mismatchTypeCounts ])

  if (isLoading) {
    return <div className="w-36"><LoadingIndicator /></div>
  }

  return (
    <Select label=""
            value={mismatchType}
            options={mismatchTypeSelectOptions}
            onChange={e => setMismatchType(e.target.value)} />
  )
}
