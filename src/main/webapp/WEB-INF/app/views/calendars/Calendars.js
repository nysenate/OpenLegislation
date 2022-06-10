import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";

import CalendarSearch from "app/views/calendars/search/CalendarSearch";
import CalendarUpdates from "app/views/calendars/updates/CalendarUpdates";
import ContentContainer from "app/shared/ContentContainer";
import SpecificCalendar from "app/views/calendars/SpecificCalendar";

export default function Calendars({ setHeaderText }) {
  const location = useLocation()

  useEffect(() => {
    if (location.pathname === '/calendars/browse') {
      setHeaderText("Browse Calendars")
    }
    if (location.pathname === '/calendars/search') {
      setHeaderText("Search For Calendars")
    }
    if (location.pathname === '/calendars/updates') {
      setHeaderText("View Calendar Updates")
    }
  }, [ location ])

  return (
    <ContentContainer>
      <Switch>
        <Route exact path="/calendars/search">
          <CalendarSearch />
        </Route>
        <Route exact path="/calendars/:year/:number">
          <SpecificCalendar setHeaderText={setHeaderText} />
        </Route>
        <Route exact path="/calendars/updates">
          <CalendarUpdates />
        </Route>
      </Switch>
    </ContentContainer>
  )
}