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
import ArticleView from "app/views/laws/chapter/ArticleView";
import SectionView from "app/views/laws/chapter/SectionView";

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
        <Route exact path="/laws/:chapterId/:articleId" >
          <ArticleView />
        </Route>
        <Route exact path="/laws/:chapterId/:articleId/:sectionId" >
          <SectionView />
        </Route>
      </Switch>
    </ContentContainer>
  )
}