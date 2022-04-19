import React from 'react';
import {
  useLocation,
  useHistory,
  useRouteMatch
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import getAgendaApi from "app/apis/getAgendaApi";
import SpecificAgendaCommittee from "app/views/agendas/SpecificAgendaCommittee"


//setHeaderText={setHeaderText}
export default function SpecificAgenda({ setHeaderText }) {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const match = useRouteMatch()

  React.useEffect(() => {
    getAgenda(match.params.agendaYear, match.params.agendaNumber)
  }, [])


  const getAgenda = (agendaYear, agendaNumber) => {
    setLoading(true)
    getAgendaApi(agendaYear, agendaNumber)
      .then((response) => {
        console.log(response)
        console.log(response.result.committeeAgendas)
        setHeaderText("Agenda " + response.result.id.number + " " + response.result.id.year)
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
          <div> Week of {response.result.weekOf} | Published {response.result.publishedDateTime} </div>
          <div> {response.result.totalAddendum} Addenda | {response.result.totalCommittees} Committee(s)
            | {response.result.totalBillsConsidered} Bills on Agenda | {response.result.totalBillsVotedOn} Bills Voted On</div>
          <p> Note: A committee may receive multiple updates (i.e. addenda) which can either overwrite prior meeting details or supplement it. </p>

          <SpecificAgendaCommittee response={response.result.committeeAgendas}/>
        </div>
      }
    </div>
  )

}