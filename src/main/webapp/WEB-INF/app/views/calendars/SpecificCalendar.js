import React from 'react';
import {
  useLocation,
  useHistory,
  useRouteMatch
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import getCalendarsApi from "app/apis/getCalendarApi";
// import ActiveLists from "app/views/calendars/SpecificCalendarActiveLists";
import Floor from "app/views/calendars/SpecificCalendarFloor";


export default function SpecificCalendar({ setHeaderText }) {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const match = useRouteMatch()

  React.useEffect(() => {
    getCalendar(match.params.calendarYear, match.params.calendarNumber)
  }, [])


  const getCalendar = (calendarYear, calendarNumber) => {
    setLoading(true)
    getCalendarsApi(calendarYear, calendarNumber)
      .then((response) => {
        // console.log(response)
        setHeaderText("Calendar " + response.result.calendarNumber + " " + response.result.year)
        setResponse(response)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.log(`${error}`)
      })
      .finally(() => {
        setLoading(false)
      })
  }


  return (
    <div className="p-3">

      {loading
        ? <LoadingIndicator />
        :
        <div>
          {/*<ActiveLists response={response} />*/}
          <Floor response={response} />
        </div>
      }
    </div>
  )

}