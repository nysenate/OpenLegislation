import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";

import AgendaSearch from "app/views/agendas/search/AgendaSearch";
import AgendaUpdates from "app/views/agendas/updates/AgendaUpdates";
import ContentContainer from "app/shared/ContentContainer";
import AgendaView from "app/views/agendas/AgendaView";
import CommitteeAgenda from "app/views/agendas/CommitteeAgenda";

export default function Agendas({ setHeaderText }) {
  const location = useLocation()

  useEffect(() => {
    if (location.pathname === '/agendas/search') {
      setHeaderText("Search For Agendas")
    }
    if (location.pathname === '/agendas/updates') {
      setHeaderText("Search Agenda Updates")
    }
  }, [ location ])

  return (
    <ContentContainer>
      <Switch>
        <Route exact path="/agendas/search">
          <AgendaSearch />
        </Route>
        <Route exact path="/agendas/:agendaYear/:agendaNumber">
          <AgendaView setHeaderText={setHeaderText} />
        </Route>
        <Route exact path="/agendas/:agendaYear/:agendaNumber/:committee">
          <CommitteeAgenda setHeaderText={setHeaderText}/>
        </Route>
        <Route exact path="/agendas/updates">
          <AgendaUpdates />
        </Route>
      </Switch>
    </ContentContainer>
  )
}