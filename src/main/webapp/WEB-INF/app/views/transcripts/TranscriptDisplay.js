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
        <pre className="text text--small">{addLinks(transcript.text, transcript.linkedBills.items)}</pre>
      </div>
    </section>
  )
}

function addLinks(text, linkedBills) {
  let textNodes = [text];

  for (const bill of linkedBills) {
    const billSplitIndex = bill.indexOf('-');
    const billPrintNo = bill.substring(0, billSplitIndex);
    const billNo = bill.substring(1, billSplitIndex);
    const billYear = bill.substring(billSplitIndex + 1);
    let pattern;

    if (bill.startsWith("S")) pattern = new RegExp(`(Senate (?:Print )?(?:Bill )?(?:Number )?${billNo}\\w?)`, 'g');
    else if (bill.startsWith("A")) pattern = new RegExp(`(Assembly (?:Print )?(?:Bill )?(?:Number )?${billNo}\\w?)`, 'g');
    else if (bill.startsWith("J")) pattern = new RegExp(`(Resolution (?:Number )?${billNo})`, 'g');
    else if (bill.startsWith("B")) pattern = new RegExp(`(Senate Concurrent Resolution (?:Number )?${billNo})`, 'g');
    else if (bill.startsWith("C")) pattern = new RegExp(`(Assembly Concurrent Resolution (?:Number )?${billNo})`, 'g');
    else continue;

    textNodes = textNodes.flatMap((node) => {
      if (typeof node !== 'string') return [node]; // skip over anything that has already been converted to a React element

      // odd indices are captured groups and should link to their respective bills, even indices are non-captured groups
      return node.split(pattern).map((segment, i) =>
        i % 2 === 1
          ? <Link to={`/bills/${billYear}/${billPrintNo}`} target="_blank" className="link">{segment}</Link>
          : segment
      );
    });
  }
  return textNodes;
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
          <span className="h5">Date & Time: </span>
          {formatDateTime(hearing.date, DateTime.DATE_FULL)}
          {hearing.startTime ? ", " + formatDateTime(hearing.startTime, DateTime.TIME_SIMPLE) : ""}
          {hearing.endTime ? " - " + formatDateTime(hearing.endTime, DateTime.TIME_SIMPLE) : ""}
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
