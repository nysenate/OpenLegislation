import React from "react";
import { useRouteMatch } from "react-router-dom";
import getSessionApi from "app/apis/getSessionApi";

export default function SessionTranscript() {
  const year = null;
  const dateTime = useRouteMatch().params.dateTime;
  const [ loading, setLoading ] = React.useState(true)
  let [sessionTranscript, setSessionTranscript] = React.useState([]);
  // TODO: catch error
  React.useEffect(() => {getSessionApi(year, dateTime)
    .then((res) => {setSessionTranscript(res); setLoading(false)})},
    [year, dateTime]);
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
