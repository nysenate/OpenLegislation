import React from "react"
import SockJS from 'sockjs-client';
import * as StompJs from "@stomp/stompjs";
import { DateTime } from "luxon";
import { Link } from "react-router-dom";
import Input from "app/shared/Input";
import * as queryString from "query-string";
import { anonymousUrl } from "app/lib/urlUtils";


export default function ApiMonitor({ setHeaderText }) {
  const [ loadDateTime, setLoadDateTime ] = React.useState(DateTime.now())
  const [ events, setEvents ] = React.useState([])
  // The total num of events received.
  const [ numEvents, setNumEvents ] = React.useState(0)
  const [ filter, setFilter ] = React.useState("")
  // This is needed so the filter value can be accessed from closures.
  const latestFilter = React.useRef(filter)

  // Initialize websocket configuration.
  React.useEffect(() => {
    setHeaderText("API Monitor")
    let client
    const stompConfig = {
      webSocketFactory: () => new SockJS("/sock"),
      reconnectDelay: 5000,
      heartbeatIncoming: 2000,
      heartbeatOutgoing: 2000,
      onConnect: () => {
        client.subscribe("/event/apiLogs", (socketEvent) => {
          const event = formatEventDates(JSON.parse(socketEvent.body))
          if (latestFilter.current.length > 0) {
            if (matchesFilter(event, latestFilter.current)) {
              setEvents((prev) => [ event, ...prev ].slice(0, 100))
            }
          } else {
            // Save a max of 100 events.
            setEvents((e) => [ event, ...e ].slice(0, 100))
          }

          setNumEvents((n) => n + 1)
        })
      },
    }

    client = new StompJs.Client(stompConfig)
    client.activate()

    return () => {
      client.deactivate()
    }
  }, [])

  const reset = () => {
    setLoadDateTime(DateTime.now())
    setEvents([])
    setNumEvents(0)
    setFilter("")
    latestFilter.current = ""
  }

  return (
    <div className="p-3">
      <div className="flex justify-between items-baseline">
        <h3 className="h3 mb-3">{numEvents} API Requests since {loadDateTime.toLocaleString(DateTime.DATETIME_MED)}</h3>
        <button className="mx-3 btn btn--secondary" onClick={() => reset()}>Reset</button>
      </div>
      <div>
        <Input label="Filter API Requests"
               value={filter}
               onChange={(e) => {
                 setFilter(e.target.value)
                 latestFilter.current = e.target.value
               }}
               type="text"
               name="filter"
               tabIndex={1}
        />
      </div>
      <div className="my-5">
        <table className="table table-fixed table--stripe w-full">
          <thead>
          <tr className="py-3">
            <th className="w-28">Time</th>
            <th className="w-32">IP</th>
            <th className="w-28">User</th>
            <th className="w-20">Method</th>
            <th className="w-20">Status</th>
            <th className="w-24">Latency</th>
            <th className="w-full">URL</th>
          </tr>
          </thead>
          <tbody>
          {events.map((e, index) => <LogEventRow event={e} key={index} />)}
          </tbody>
        </table>
      </div>
      <div>
        {(numEvents - events.length !== 0) &&
          <span>{numEvents - events.length} Logs Truncated</span>
        }
      </div>
    </div>
  )
}

function LogEventRow({ event }) {
  return (
    <tr className="text-sm">
      <td>{event.logDateTime.toLocaleString(DateTime.TIME_WITH_SECONDS)}</td>
      <td>{event.apiResponse.baseRequest.ipAddress}</td>
      <td>{event.apiResponse.baseRequest.apiUser?.name || "None"}</td>
      <td>{event.apiResponse.baseRequest.requestMethod}</td>
      <td>{event.apiResponse.statusCode}</td>
      <td>{event.apiResponse.processTime}</td>
      <td className="whitespace-nowrap text-ellipsis truncate">
        <Link to={`${anonymousUrl(event.apiResponse.baseRequest.url)}`} target="_blank" className="link">
          {event.apiResponse.baseRequest.url}
        </Link>
      </td>
    </tr>
  )
}

/**
 * Filters an event for a string.
 * @param event The event to be filtered.
 * @param filter The filter to apply.
 * @returns {boolean} true if a field in event contains the filter text.
 */
const matchesFilter = (event, filter) => { // TODO Matching URL does not work?
  const filterUpper = filter.toUpperCase()
  return event.logDateTime.toLocaleString(DateTime.TIME_WITH_SECONDS).indexOf(filterUpper) > -1
    || event.apiResponse.baseRequest.ipAddress.indexOf(filterUpper) > -1
    || event.apiResponse.baseRequest.apiUser?.name.toUpperCase().indexOf(filterUpper) > -1
    || event.apiResponse.baseRequest.requestMethod.toUpperCase().indexOf(filterUpper) > -1
    || event.apiResponse.statusCode.toString().indexOf(filterUpper) > -1
    || event.apiResponse.processTime.toString().indexOf(filterUpper) > -1
    || event.apiResponse.baseRequest.url.toUpperCase().indexOf(filterUpper) > -1
}

/**
 * Converts all event's dates to DateTime objects.
 */
const formatEventDates = (event) => {
  event.logDateTime = arrayToDateTime(event.logDateTime)
  event.requestTime = arrayToDateTime(event.requestTime)
  event.apiResponse.responseDateTime = arrayToDateTime(event.apiResponse.responseDateTime)
  event.apiResponse.baseRequest.requestTime = arrayToDateTime(event.apiResponse.baseRequest.requestTime)
  return event
}

/**
 * Convert a "date time array", i.e. [2022,2,10,16,45,38,470327833], into a DateTime object.
 * This converts the weird date format from our api log event into a usable object.
 * @param array
 * @returns {DateTime}
 */
const arrayToDateTime = (array) => {
  const dateObj = {
    year: array[0],
    month: array[1],
    day: array[2],
    hour: array[3],
    minute: array[4],
    second: array[5],
  }
  return DateTime.fromObject(dateObj)
}
