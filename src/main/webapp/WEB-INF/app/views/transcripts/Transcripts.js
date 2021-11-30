import React from 'react'
import {
  Route,
  Switch,
  useRouteMatch
} from "react-router-dom";
import ContentContainer from "app/shared/ContentContainer";
import SessionTranscript from "app/views/transcripts/SessionTranscript";
import HearingTranscript from "app/views/transcripts/HearingTranscript";
import SessionSearchResults from "app/views/transcripts/SessionSearchResults";
import HearingSearchResults from "app/views/transcripts/HearingSearchResults";
import getTranscript from "app/apis/transcriptApi";

export default function Transcripts() {
  return (
    <ContentContainer>
      <Switch>
        <Route exact path = "/transcripts/session">
          <SessionSearchResults/>
        </Route>
        <Route exact path = "/transcripts/hearing">
          <HearingSearchResults/>
        </Route>
        <Route path = "/transcripts/hearing/:key">
          <Transcript isHearing = {true}/>
        </Route>
        <Route path = "/transcripts/session/:key">
          <Transcript isHearing = {false}/>
        </Route>
      </Switch>
    </ContentContainer>
  )
}

function Transcript({isHearing}) {
  const id = useRouteMatch().params.key;
  const [loading, setLoading] = React.useState(true)
  const [transcript, setTranscript] = React.useState([]);
  React.useEffect(() => {getTranscript(isHearing, id)
      .then((res) => {setTranscript(res.result); setLoading(false)})
      .catch((error) => {
        // TODO: handle errors
        console.warn(`${error}`)
      })},
    [isHearing, id]);
  if (loading)
    return (<div>Loading ...</div>);
  else if (isHearing)
    return <HearingTranscript hearing = {transcript}/>
  else
    return <SessionTranscript session = {transcript}/>
}
