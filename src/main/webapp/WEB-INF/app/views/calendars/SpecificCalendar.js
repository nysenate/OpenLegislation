import React from 'react';
import {
  useParams
} from "react-router-dom";
import { fetchCalendar } from "app/apis/calendarApi";
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";
import Tabs from "app/shared/Tabs";
import ActiveList from "app/views/calendars/ActiveList";


// TODO
// - Loading
// - error message
// - set initial selected tab

export default function SpecificCalendar({ setHeaderText }) {
  const [ response, setResponse ] = React.useState()
  const [ loading, setLoading ] = React.useState(true)
  const [ tabs, setTabs ] = React.useState([])
  const [ activeTab, setActiveTab ] = React.useState("")
  const { year, number } = useParams()

  React.useEffect(() => {
    getCalendar(year, number)
  }, [])

  React.useEffect(() => {
    if (response) {
      const calDate = formatDateTime(DateTime.fromISO(response.result.calDate), DateTime.DATE_MED)
      setHeaderText(`Senate Calendar #${response.result.calendarNumber} - ${calDate}`)

      setTabs([
        {
          name: "Active List",
          quantity: response.result.activeLists.size || null,
          isDisable: false,
          component: <ActiveList activeList={response.result.activeLists} />
        }
      ])
    }
  }, [ response ])

  // TODO how to handle invalid url? Error message or 404?
  const getCalendar = (year, number) => {
    setLoading(true)
    setResponse(null)
    fetchCalendar(year, number)
      .then(res => setResponse(res))
      .catch((error) => {
        // TODO properly handle errors
        console.log(`${error}`)
      })
      .finally(() => setLoading(false))
  }

  return (
    <div className="my-6">
      <Tabs tabs={tabs}
            activeTab={activeTab}
            setActiveTab={tab => setActiveTab(tab)} />
    </div>
  )
}
