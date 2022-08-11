import React from 'react'
import { Link } from "react-router-dom";
import {
  DATETIME_FULL_NO_ZONE,
  formatDateTime
} from "app/lib/dateUtils";
import { getTranscript } from "app/apis/transcriptApi";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { DateTime } from "luxon";


/**
 * Top-level method for displaying a single session or hearing transcript.
 */
export default function TranscriptDisplay({ id, isHearing, setHeaderText }) {
  const [ loading, setLoading ] = React.useState(true)
  const [ transcript, setTranscript ] = React.useState([])

  React.useEffect(() => {
    setLoading(true)
    getTranscript(isHearing, id)
      .then((res) => {
        setTranscript(res.result)
        setHeaderText(isHearing
          ? `Public Hearing Transcript: ${formatDateTime(res.result.date, DateTime.DATE_FULL)}`
          : `Session Transcript: ${formatDateTime(res.result.dateTime, DateTime.DATE_FULL)}`)
      })
      .finally(() => setLoading(false))
  }, [ isHearing, id ]);

  if (loading)
    return <LoadingIndicator />

  return (
    <section className="p-3">
      <Link to={`/transcripts/${isHearing ? "hearing" : "session"}`} className="link">
        Back to search
      </Link>
      {isHearing ? <HearingHeading hearing={transcript} /> : <SessionHeading session={transcript} />}
      <div className="my-3">
        <pre className="text text--small">{transcript.text}</pre>
      </div>
    </section>
  )
}

function SessionHeading({ session }) {
  return (
    <div className="my-6">
      <h3 className="h5 my-3">{session.sessionType}</h3>
      {formatDateTime(session.dateTime, DATETIME_FULL_NO_ZONE)}, {session.location}
    </div>
  )
}

function HearingHeading({ hearing }) {
  return (
    <div>
      <div>
        <h3 className="h5 my-3">{hearing.title}</h3>
        <div className="my-3">
          <span className="h5">Date: </span>
          {formatDateTime(hearing.date, DateTime.DATE_FULL)},
          &nbsp;{formatDateTime(hearing.startTime, DateTime.TIME_SIMPLE)}
        </div>
        <div className="my-3 flex">
          <span className="h5 mr-1">Address:</span> <span className="whitespace-pre-wrap">{hearing.address} </span>
        </div>
      </div>
      <div className="mt-8">
        {hearing.committees.map((host) =>
          <div key={host.chamber + host.name}>{host.chamber} {host.type} {host.name}</div>)
        }
      </div>
    </div>
  )
}
