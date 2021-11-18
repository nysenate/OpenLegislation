import React from 'react'
import {
  Route,
  Switch
} from "react-router-dom";
import ContentContainer from "app/shared/ContentContainer";
import SessionTranscript from "app/views/transcripts/SessionTranscript";
import HearingTranscript from "app/views/transcripts/HearingTranscript";

export default function Transcripts() {
  return (
    <ContentContainer>
      <Switch>
        <Route path = "/transcripts/session/:dateTime">
          <SessionTranscript/>
        </Route>
        <Route path = "/transcripts/hearing/:id">
          <HearingTranscript/>
        </Route>
      </Switch>
    </ContentContainer>
  )
}
