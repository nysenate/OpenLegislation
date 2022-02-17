import React from 'react'
import { Link } from "react-router-dom";
import { formatDateTime } from "app/lib/dateUtils";
import { getTranscript } from "app/apis/transcriptApi";

const dateOptions = {year: "numeric", month: "long", day: "numeric"}
const timeOptions = {hour: 'numeric', minute: 'numeric'}

export default function TranscriptDisplay({id, isHearing}) {
  const [loading, setLoading] = React.useState(true)
  const [transcript, setTranscript] = React.useState([]);
  React.useEffect(() => {getTranscript(isHearing, id)
      .then((res) => setTranscript(res.result))
      .finally(() => setLoading(false))},
    [isHearing, id]);
  if (loading)
    return (<div>Loading ...</div>);

  return (
    <section>
      <p className = "gray-2-blue">
        <Link to = {`/transcripts/${isHearing ? "hearing" : "session"}`}>
          Back to search
        </Link>
      </p><br/>
      {isHearing ? <HearingHeading hearing = {transcript}/> : <SessionHeading session = {transcript}/>}
      <div className = "my-3">
        <pre className = "text text--small">{transcript.text}</pre>
      </div>
    </section>
  )
}

function SessionHeading({session}) {
  return (
    <h2>
      <strong>{session.sessionType}</strong><br/>
      {getDisplayDate(session, false)}, {session.location}
    </h2>
  )
}

function HearingHeading({hearing}) {
  return (
    <div>
      <h2>
        <strong>{hearing.title}</strong><br/><br/>
        <strong>Date:</strong> {getDisplayDate(hearing, true)}<strong> Time:</strong> {getHearingDisplayTime(hearing)}<br/><br/>
        <strong>Address:</strong> {hearing.address}<br/><br/>
      </h2>
      <h4>
        {hearing.committees.map((host) =>
          <div key = {host.chamber + host.name}>{host.chamber} {host.type} {host.name} </div>)}
      </h4>
    </div>
  )
}

function getHearingDisplayTime(hearing) {
  if (!hearing.startTime)
    return ""
  let str = formatDateTime(hearing.startTime, timeOptions)
  if (hearing.endTime)
    str += " - " + formatDateTime(hearing.endTime, timeOptions)
  return str
}

export function getDisplayDate(transcript, isHearing) {
  let options = dateOptions;
  if (!isHearing)
    options = {...options, ...timeOptions}
  return formatDateTime(transcript[isHearing ? "date" : "dateTime"], options)
}
