import React from 'react';
import {
  useLocation,
  useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import AgendaSearchForm from "app/views/agendas/AgendaSearchForm";
import AgendaSearchResults from "app/views/agendas/AgendaSearchResults";
import agendaSearchApi from "app/apis/agendaSearchApi";
import { fetchYearlyAgendaList } from "app/apis/agendaApi";

export default function AgendaSearch() {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ responseForForm, setResponseForForm ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const page = params.page || 1

  let date = new Date();

  const [ year, setYear ] = React.useState("" + date.getFullYear())
  const [ sort, setSort ] = React.useState('desc')
  const [ weekOf, setWeekOf ] = React.useState('any')
  const [ agendaNumber, setAgendaNumber ] = React.useState('any')
  const [ committee, setCommittee ] = React.useState('any')
  const [ baseBillNoInput, setBaseBillNoInput ] = React.useState('')
  const [ meetingNotes, setMeetingNotes ] = React.useState('')

  const [ limit, setLimit ] = React.useState(6)
  const [ offset, setOffset ] = React.useState((page - 1) * limit + 1)

  React.useEffect(() => {
    doSearch()
  }, [ location, year, sort, weekOf, agendaNumber, committee ])

  const setSearchValues = (year, sortSelect, weekOfSelect, agendaNumberSelect, committeeSelect, baseBillNoInput, meetingNotes) => {
    setYear(year)
    setSort(sortSelect)
    setWeekOf(weekOfSelect)
    setAgendaNumber(agendaNumberSelect)
    setCommittee(committeeSelect)
    setBaseBillNoInput(baseBillNoInput)
    setMeetingNotes(meetingNotes)
  }

  const doSearch = () => {
    setLoading(true)

    fetchYearlyAgendaList(year)
      .then((response) => {
        // console.log(response)
        setResponseForForm(response)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`)
      })
      .finally(() => {
        setLoading(false)
      })

    agendaSearchApi(year, sort, weekOf, agendaNumber, committee, meetingNotes, baseBillNoInput, limit, offset)
      .then((response) => {
        // console.log(response)
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
    setOffset(pageInfo.offset)
  }

  //send in filters into agendaSearchResults
  return (
    <div className="p-3">
      <AgendaSearchForm updateValues={setSearchValues} results={responseForForm} />
      {loading
        ? <LoadingIndicator />
        :
        <div>
          <AgendaSearchResults response={response} limit={limit} page={page} onPageChange={onPageChange} />
        </div>
      }
    </div>
  )

}