import React from "react";

export default function SessionTranscript({session}) {
  return (<section>
      <div>
        <h2>
          <strong>{session.sessionType}</strong>
          <br/>{session.dateTime}, {session.location}
        </h2>
      </div>
      <div className = "my-3">
        <pre className = "text text--small">
          {session.text}
        </pre>
      </div>
    </section>
  )
}
