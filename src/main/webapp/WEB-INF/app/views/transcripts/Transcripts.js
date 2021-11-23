import React from 'react'
import {
  Route,
  Switch
} from "react-router-dom";
import ContentContainer from "app/shared/ContentContainer";
import SessionTranscript from "app/views/transcripts/SessionTranscript";
import HearingTranscript from "app/views/transcripts/HearingTranscript";
import TranscriptSearchResults from "app/views/transcripts/TranscriptSearchResults";

export default function Transcripts() {
  return (
    <ContentContainer>
      <Switch>
        <Route exact path = "/transcripts/session">
          <TranscriptSearchResults/>
        </Route>
        // TODO: should be a param you can select from dropdown
        <Route path = "/transcripts/session/:year(\d+)">
          <TranscriptSearchResults/>
        </Route>
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
