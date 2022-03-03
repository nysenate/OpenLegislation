import React from 'react';
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import CalendarBrowseForm from "app/views/calendars/CalendarBrowseForm";
import CalendarBrowseResults from "app/views/calendars/CalendarBrowseResults";
import calendarBrowseApi from "app/apis/calendarBrowseApi";


export default function CalendarSearch() {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const page = params.page || 1

  let date = new Date();


  const [ year, setYear ] = React.useState("" + date.getFullYear())

  const [ month, setMonth ] = React.useState(date.getMonth)

  React.useEffect(() => {
    doSearch()
  }, [ year, month ]) //todo maybe remove month in this use effect

  const setSearchValues = (year, month) => {
    setYear(year)
    setMonth(month)
  }

  const doSearch = () => {
    setLoading(true)

    calendarBrowseApi(year)
      .then((response) => {
        console.log(response)
        setResponse(response)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`)
      })
      .finally(() => {
        setLoading(false)
      })


  }

  // const onPageChange = pageInfo => {
  //   params.page = pageInfo.selectedPage
  //   history.push({search: queryString.stringify(params)})
  //   setOffset((page - 1) * limit + 1)
  // }

  return (
    <div className="p-3">
      <CalendarBrowseForm updateValues={setSearchValues} />
      {loading
        ? <LoadingIndicator />
        :
        <div>
          <CalendarBrowseResults response={response} />
        </div>
      }
    </div>
  )

}