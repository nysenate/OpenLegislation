import React from "react"
import {
  getDataProcessLogs,
  runDataProcess
} from "app/apis/logsApi";
import { DateTime } from "luxon";
import DatePicker from "app/shared/DatePicker";
import { formatDateTime } from "app/lib/dateUtils";
import Pagination, { PageParams } from "app/shared/Pagination";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { FormCheckbox } from "app/shared/Checkbox";


const defaultLimit = 16

export default function DataProcessLog({ setHeaderText }) {
  const [ logsResponse, setLogsResponse ] = React.useState()
  const [ from, setFrom ] = React.useState(DateTime.now().minus({ days: 2 }).startOf("day"))
  const [ to, setTo ] = React.useState(DateTime.now())
  const [ hideEmpty, setHideEmpty ] = React.useState(true)
  const [ pageParams, setPageParams ] = React.useState(new PageParams(1, defaultLimit))

  React.useEffect(() => {
    setHeaderText("Data Process Logs")
  }, [])

  React.useEffect(() => {
    setPageParams(new PageParams(1, defaultLimit))
  }, [ from, to, hideEmpty ])

  React.useEffect(() => {
    fetchDataProcessLogs(pageParams.limit, pageParams.offset)
  }, [ pageParams ])

  const onPageChange = (pageParams) => {
    setPageParams(pageParams)
  }

  const fetchDataProcessLogs = (limit, offset) => {
    setLogsResponse(undefined)
    getDataProcessLogs(from, to, { full: !hideEmpty, limit: limit, offset: offset })
      .then(res => setLogsResponse(res))
  }

  return (
    <section className="p-3">
      <div className="flex items-center">
        <div className="mr-5">
          <DatePicker label="From"
                      name="from"
                      date={from}
                      maxDate={to}
                      setDate={date => setFrom(date)}
                      showTimeSelect
                      selectsStart />
        </div>
        <div className="mr-5">
          <DatePicker label="To"
                      name="to"
                      date={to}
                      maxDate={DateTime.now()}
                      setDate={date => setTo(date)}
                      showTimeSelect
                      selectsEnd />
        </div>
        <div>
          <FormCheckbox label="Hide empty runs"
                        value={hideEmpty}
                        onChange={e => setHideEmpty(e.target.checked)}
                        name="hideempty" />
        </div>
        <div className="ml-auto">
          <button className="btn btn--primary" onClick={() => runDataProcess()}>Execute Data Process</button>
        </div>
      </div>

      <div className="mt-6 mb-3">
        {logsResponse?.result?.items &&
          <div>
            <div className="text-center">
              <span className="font-semibold">{logsResponse.total}</span> data process runs found.
            </div>
            <DataProcessLogSearchResults logs={logsResponse.result.items}
                                         total={logsResponse.total}
                                         pageParams={pageParams}
                                         onPageChange={onPageChange} />
          </div>
        }
      </div>
      {!logsResponse &&
        <LoadingIndicator />
      }
    </section>
  )
}

function DataProcessLogSearchResults({ logs, total, pageParams, onPageChange }) {
  return (
    <div>
      <Pagination currentPage={pageParams.selectedPage}
                  limit={pageParams.limit}
                  total={total}
                  onPageChange={onPageChange} />
      {logs.map(log => <DataProcessLogResult log={log} key={log.run.id} />)}
      <Pagination currentPage={pageParams.selectedPage}
                  limit={pageParams.limit}
                  total={total}
                  onPageChange={onPageChange} />
    </div>
  )
}

function DataProcessLogResult({ log }) {
  return (
    <div className="">
      <div>
        <span className="h5">Run #{log.run.id}</span> started
        on <span className="h5">{formatDateTime(log.run.startDateTime, DateTime.DATETIME_MED)}</span> and ended
        on <span className="h5">{log.run.endDateTime ? formatDateTime(log.run.endDateTime, DateTime.DATETIME_MED) : "null"}</span>.
      </div>
      <div className="mb-3 text text--small">
        Invoked by {log.run.invokedBy}
      </div>
      {log.run.exceptions &&
        <div>
          <pre className="text-xs text-red-800 overflow-x-auto">
          {log.run.exceptions}
          </pre>
        </div>
      }
      <div>
        {log.first &&
          <div><span className="h5">First Processed:</span> {log.first.sourceId}</div>
        }
        {log.last &&
          <div><span className="h5">Last Processed:</span> {log.last.sourceId}</div>
        }
      </div>
      <hr className="my-6" />
    </div>
  )
}

