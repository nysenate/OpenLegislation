import React from 'react'
import ContentContainer from "app/shared/ContentContainer";
import {
  Route,
  Switch
} from "react-router-dom";
import SessionTranscript from "app/views/transcripts/SessionTranscript"

export default function Transcripts() {

  return (
    <ContentContainer>
      <Switch>
        // TODO: fix path
        <Route exact path="/transcripts/search">
          <SessionTranscript/>
        </Route>
      </Switch>
    </ContentContainer>
  )
}