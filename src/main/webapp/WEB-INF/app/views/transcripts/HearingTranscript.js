import React from "react";

export default function HearingTranscript({hearing}) {
  return (<section>
    <div>
      <h2>
        <strong>hearing.title</strong><br/>
        Date: {hearing.date} Time: {hearing.startTime} - {hearing.endTime}
        Address: {hearing.address}
        {hearing.committees.map((host) => <div>{host}, </div>)}
      </h2>
    </div>
    <div className = "my-3">
        <pre className = "text text--small">
          {hearing.text}
        </pre>
    </div>
  </section>);
}
