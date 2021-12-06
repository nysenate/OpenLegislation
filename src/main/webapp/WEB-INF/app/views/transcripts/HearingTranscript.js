import React from "react";

export default function HearingTranscript({hearing}) {
  return (<section>
    <div>
      <h2>
        <strong>{hearing.title}</strong><br/>
        <b>Date:</b> {hearing.date} <b>Time:</b> {hearing.startTime} - {hearing.endTime}<br/>
        <b>Address:</b> {hearing.address}
        {hearing.committees.map((host) => <div key = {host}>{host.chamber} {host.type} {host.name} </div>)}
      </h2>
    </div>
    <div className = "my-3">
        <pre className = "text text--small">
          {hearing.text}
        </pre>
    </div>
  </section>);
}
