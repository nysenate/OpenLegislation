import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";
import LawSearch from "app/views/laws/search/LawSearch";
import ChapterView from "app/views/laws/chapter/ChapterView";
import LawUpdates from "app/views/laws/updates/LawUpdates";
import ContentContainer from "app/shared/ContentContainer";

export default function Laws({ setHeaderText }) {
  const location = useLocation()

  useEffect(() => {
    if (location.pathname === '/laws') {
      setHeaderText("Search NYS Laws")
    }
    if (location.pathname === '/laws/updates') {
      setHeaderText("Search NYS Law Updates")
    }
  }, [ location ])

  return (
    <ContentContainer>
      <Switch>
        <Route exact path="/laws">
          <LawSearch />
        </Route>
        <Route exact path="/laws/updates">
          <LawUpdates />
        </Route>
        <Route path="/laws/:lawId">
          <ChapterView setHeaderText={setHeaderText} />
        </Route>
      </Switch>
    </ContentContainer>
  )
}