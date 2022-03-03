import React from 'react';
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import CalendarSearchForm from "app/views/calendars/CalendarSearchForm";
import CalendarSearchResults from "app/views/calendars/CalendarSearchResults";
import calendarSearchApi from "app/apis/calendarSearchApi";


export default function CalendarSearch() {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const page = params.page || 1

  let date = new Date();


  const [ year, setYear ] = React.useState("" + date.getFullYear())
  const [ searchBy, setSearchBy ] = React.useState('calDate')
  const [ searchValue, setSearchValue ] = React.useState('')
  const [ sort, setSort ] = React.useState('desc')
  const [ activeListOnly, setActiveListOnly ] = React.useState(false)

  const [ limit, setLimit ] = React.useState(6)
  const [ offset, setOffset ] = React.useState((page - 1) * limit + 1)

  React.useEffect(() => {
    doSearch()
  }, [ location, year, searchBy, searchValue, sort, activeListOnly ])

  const setSearchValues = (year, searchBy, searchValue, sortSelect, activeListOnly) => {
    setYear(year)
    setSearchBy(searchBy)
    setSearchValue(searchValue)
    setSort(sortSelect)
    setActiveListOnly(activeListOnly)
  }

  const doSearch = () => {
    setLoading(true)

    calendarSearchApi(searchValue, activeListOnly, year, sort, searchBy, limit, offset)
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
    history.push({ search: queryString.stringify(params) })
    setOffset((page - 1) * limit + 1)
  }

  return (
    <div className="p-3">
      <CalendarSearchForm updateValues={setSearchValues} />
      {loading
        ? <LoadingIndicator />
        :
        <div>
          <CalendarSearchResults response={response} limit={limit} page={page} onPageChange={onPageChange}
                                 activeListOnly={activeListOnly} />
        </div>
      }
    </div>
  )

}