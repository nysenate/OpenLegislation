import React from 'react'
import { getTranscript } from "app/apis/transcriptApi";

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

  return <section>
    <div>
      {isHearing ? <HearingHeading hearing = {transcript}/> : <SessionHeading session = {transcript}/>}
    </div>
    <div className = "my-3">
      <pre className = "text text--small">
        {transcript.text}
      </pre>
    </div>
  </section>
}

function SessionHeading({session}) {
  return <h2>
    <strong>{session.sessionType}</strong>
    <br/>{getDisplayDate(session, false)}, {session.location}
  </h2>
}

function HearingHeading({hearing}) {
  return <h2>
    <strong>{hearing.title}</strong><br/><br/>
    <b>Date:</b> {getDisplayDate(hearing, true)}<b> Time:</b> {getHearingDisplayTime(hearing)}<br/><br/>
    <b>Address:</b> {hearing.address}<br/>{hearing.committees.map((host) =>
      <div key = {host.chamber + host.name}>{host.chamber} {host.type} {host.name} </div>)}
  </h2>
}

function getHearingDisplayTime(hearing) {
  if (!hearing.startTime)
    return ""
  // There is no Javascript object for a Time alone, so a date must be added.
  const defaultDate = "1970-01-01"
  const dateTimeFormat = new Intl.DateTimeFormat("en-US", timeOptions)
  const startTime = new Date(defaultDate + "T" + hearing.startTime)
  let str = dateTimeFormat.format(startTime)
  if (hearing.endTime) {
    const endTime = new Date(defaultDate + "T" + hearing.endTime)
    str += " - " + dateTimeFormat.format(endTime)
  }
  return str
}

export function getDisplayDate(transcript, isHearing) {
  let options = {year: "numeric", month: "long", day: "numeric"};
  if (!isHearing)
    options = {...options, ...timeOptions}
  // Dates without times default to using UTC.
  else
    options.timeZone = "UTC"

  const dateTimeFormat = new Intl.DateTimeFormat("en-US", options)
  const value = new Date(transcript[isHearing ? "date" : "dateTime"])
  return dateTimeFormat.format(value)
}
