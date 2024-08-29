import React from 'react'
import { Link } from "react-router-dom";
import {
  DATETIME_FULL_NO_ZONE,
  formatDateTime
} from "app/lib/dateUtils";
import { getTranscript } from "app/apis/transcriptApi";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { DateTime } from "luxon";
import { FilePdf } from "phosphor-react";


/**
 * Top-level method for displaying a single session or hearing transcript.
 */
export default function TranscriptDisplay({ params, isHearing, setHeaderText }) {
  const [ loading, setLoading ] = React.useState(true)
  const [ transcript, setTranscript ] = React.useState([])

  React.useEffect(() => {
    setLoading(true)
    getTranscript(isHearing, params)
      .then((res) => {
        setTranscript(res.result)
        setHeaderText(isHearing
          ? `Hearing Transcript: ${formatDateTime(res.result.date, DateTime.DATE_FULL)}`
          : `Session Transcript: ${formatDateTime(res.result.dateTime, DateTime.DATE_FULL)}`)
      })
      .finally(() => setLoading(false))
  }, [ isHearing, params ]);

  if (loading)
    return <LoadingIndicator />
  let pdfUrl;
  if (isHearing) {
    pdfUrl = `/pdf/hearings/${transcript.id}`
  }
  else {
    pdfUrl = `/pdf/transcripts/${transcript.dateTime}/${transcript.sessionType}`
  }

  return (
    <section className="p-3">
      <div className="flex w-1/2">
        <div className="flex-grow mr-8">
          <Link to={`/transcripts/${isHearing ? "hearing" : "session"}`} className="link">
            Back to search
          </Link>
        </div>
        <div className="mr-8">
          <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
          <Link to={`${pdfUrl}`} target="_blank" className="link">
            View as PDF
          </Link>
        </div>
      </div>
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
