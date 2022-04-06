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
import LawNodeView from "app/views/laws/chapter/LawNodeView";
import LawLeafNodeView from "app/views/laws/chapter/LawLeafNodeView";

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
        <Route exact path="/laws/:chapterId">
          <ChapterView setHeaderText={setHeaderText} />
        </Route>
        <Route exact path="/laws/:chapterId/node/:locationId">
          <LawNodeView setHeaderText={setHeaderText} />
        </Route>
        <Route exact path="/laws/:chapterId/leaf/:locationId">
          <LawLeafNodeView setHeaderText={setHeaderText} />
        </Route>
      </Switch>
    </ContentContainer>
  )
}