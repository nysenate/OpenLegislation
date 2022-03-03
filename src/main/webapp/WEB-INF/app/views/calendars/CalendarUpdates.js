import React from 'react';
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import CalendarUpdatesForm from "app/views/calendars/CalendarUpdatesForm";
import CalendarUpdatesResults from "app/views/calendars/CalendarUpdatesResults";
import getCalendarUpdatesApi from "app/apis/getCalendarUpdatesApi";

export default function CalendarUpdates() {
  const [ loading, setLoading ] = React.useState(true)
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const page = params.page || 1

  let aWeekAgo = new Date();
  aWeekAgo.setDate(aWeekAgo.getDate() - 7);

  const [ from, setFrom ] = React.useState(aWeekAgo.toISOString().slice(0, 10))
  const [ to, setTo ] = React.useState(new Date().toISOString().slice(0, 10))

  const [ using, setUsing ] = React.useState('published')
  const [ sort, setSort ] = React.useState('desc')

  const [ detail, setDetail ] = React.useState(true)

  const [ limit, setLimit ] = React.useState(6)
  const [ offset, setOffset ] = React.useState(1)


  React.useEffect(() => {
    doSearch()
  }, [ location, from, to, using, sort, detail ])

  const setSearchValues = (using, sortSelect, showDetail, updateFrom, updateTo) => {
    setUsing(using)
    setSort(sortSelect)
    setDetail(showDetail)
    setFrom(updateFrom)
    setTo(updateTo)
    // doSearch();
  }

  const doSearch = () => {
    setLoading(true)
    getCalendarUpdatesApi(detail, from + 'T00:00:00.000', to + 'T00:00:00.000', sort.toUpperCase(), using, limit, offset)
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

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    setOffset(pageInfo.offset)
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="p-3">
      <CalendarUpdatesForm updateValues={setSearchValues} aWeekAgo={from} todaysDate={to} />
      {loading
        ? <LoadingIndicator />
        :
        <div>
          <CalendarUpdatesResults response={response}
                                  limit={limit}
                                  page={page}
                                  onPageChange={onPageChange}
                                  detail={detail} />
        </div>
      }
    </div>
  )
}

