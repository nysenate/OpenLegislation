import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";

import AgendaSearch from "app/views/agendas/AgendaSearch";
import AgendaUpdates from "app/views/agendas/AgendaUpdates";
import ContentContainer from "app/shared/ContentContainer";
import SpecificAgenda from "app/views/agendas/SpecificAgenda";

export default function Agendas({ setHeaderText }) {
  const location = useLocation()

  useEffect(() => {
    if (location.pathname === '/agendas/browse') {
      setHeaderText("Browse Agendas")
    }
    if (location.pathname === '/agendas/search') {
      setHeaderText("Search For Agendas")
    }
    if (location.pathname === '/agendas/updates') {
      setHeaderText("View Agenda Updates")
    }
  }, [ location ])

  return (
    <ContentContainer>
      <Switch>
        <Route exact path="/agendas/search">
          <AgendaSearch />
        </Route>
        <Route path="/agendas/view/:agendaYear/:agendaNumber">
          <SpecificAgenda setHeaderText={setHeaderText} />
        </Route>
        <Route exact path="/agendas/updates">
          <AgendaUpdates />
        </Route>
      </Switch>
    </ContentContainer>
  )
}