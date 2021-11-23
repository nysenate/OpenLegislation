import React from "react";
import { useRouteMatch } from "react-router-dom";
import getTranscript from "app/apis/transcriptApi";

export default function SessionTranscript() {
  const dateTime = useRouteMatch().params.dateTime;
  const [loading, setLoading] = React.useState(true)
  const [sessionTranscript, setSessionTranscript] = React.useState([]);
  React.useEffect(() => {getTranscript(false, dateTime)
    .then((res) => {setSessionTranscript(res.result); setLoading(false)})
      .catch((error) => {
        // TODO: handle errors
        console.warn(`${error}`)
      })},
    [dateTime]);
  if (loading)
    return (<div>Loading ...</div>);
  return (<section>
      <div>
        <h2>
          <strong>{sessionTranscript.sessionType}</strong>
          <br/>{sessionTranscript.dateTime}, {sessionTranscript.location}
        </h2>
      </div>
      <div className = "my-3">
        <pre className = "text text--small">
          {sessionTranscript.text}
        </pre>
      </div>
    </section>
  );
}
