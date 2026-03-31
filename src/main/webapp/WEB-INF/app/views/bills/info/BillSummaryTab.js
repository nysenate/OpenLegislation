import React from 'react'
import { Link } from "react-router-dom";
import BillListingQueried from "app/shared/BillListingQueried";
import { BillLawLink, BillLawChapterLink } from "app/shared/BillLawLink";


export default function BillSummaryTab({ bill, selectedAmd }) {
  return (
    <div className="m-5">
      <SameAs bill={bill} selectedAmd={selectedAmd} />
      <EnactingClause bill={bill} selectedAmd={selectedAmd} />
      <Summary bill={bill} />
      <AffectedLaw amendment={bill.amendments.items[selectedAmd]} billStatus={bill.status} />
      <AgendaCalendarReferences bill={bill} />
      <TranscriptReferences amendment={bill.amendments.items[selectedAmd]} />
      <PreviousVersions bill={bill} />
    </div>
  )
}

function SameAs({ bill, selectedAmd }) {
  if (bill.amendments.items[selectedAmd].sameAs.size === 0) {
    return null;
  }
  return (
    <section>
      <header>
        <h3 className="h5">Same As Bills</h3>
      </header>
      <div className="mx-2">
        {bill.amendments.items[selectedAmd].sameAs.items.map((sameAs) => {
            return <BillListingQueried billInfo={sameAs} key={sameAs.basePrintNo} />
          }
        )}
      </div>
    </section>
  )
}

function EnactingClause({ bill, selectedAmd }) {
  if (bill.billType.resolution) {
    return null
  }

  return (
    <section className="mt-5">
      <header>
        <h3 className="h5">Enacting Clause</h3>
      </header>
      <div className="mx-5 my-3">
        <p className="text whitespace-pre-wrap">
          {bill.amendments.items[selectedAmd].actClause || "Not Available"}
        </p>
      </div>

    </section>
  )
}

function Summary({ bill }) {
  if (bill.billType.resolution) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Summary of Bill</h3>
      </header>
      <div className="mx-5 my-3">
        <p className="text whitespace-pre-wrap">
          {bill.summary || "Not Available"}
        </p>
      </div>
    </section>
  )
}

function AffectedLaw({ amendment, billStatus }) {
  // TODO: determine whether bill is passed and find date of original and new law
  // TODO: should substituted bills have the same links
  const isPassed = billStatus.statusType === 'ADOPTED' || billStatus.statusType === 'SIGNED_BY_GOV' || billStatus.statusType === 'POCKET_APPROVAL'
  const oldDate = new Date(billStatus.actionDate)
  oldDate.setDate(oldDate.getDate() - 21)
  const newDate = new Date(billStatus.actionDate)
  newDate.setDate(newDate.getDate() + 21)
  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Affected Law</h3>
      </header>
      <div className="mx-5 my-3">
        <BillLawChapterLink lawChapter={amendment.lawSection} />
      </div>
      {amendment.relatedLaws.size !== 0 &&
      <div className="mx-5 my-3">
        <p className="text">Related Laws:</p>
        {Object.entries(amendment.relatedLaws.items).map(([ key, value ]) => {
          return (
            <p key={key}>
              {key}&nbsp;
              {value.items.map((law, index) => {
                return (
                  <React.Fragment key={law}>
                    {index !== 0 &&
                    <span>,&nbsp;</span>
                    }
                    <BillLawLink action={key} isPassed={isPassed} law={law} oldDate={oldDate} newDate={newDate} />
                  </React.Fragment>
                )
              })}
            </p>
          )
        })}
      </div>
      }
    </section>
  )
}

function PreviousVersions({ bill }) {
  if (bill.previousVersions.size === 0) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Previous Versions of this Bill</h3>
      </header>
      <div className="mx-2">
        {bill.previousVersions.items.map((sameAs) => {
            return <BillListingQueried billInfo={sameAs} key={sameAs.basePrintNo} />
          }
        )}
      </div>
    </section>
  )
}

function AgendaCalendarReferences({ bill }) {
  if (bill.calendars.size === 0 && bill.committeeAgendas.size === 0) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Agenda/Calendar References</h3>
      </header>
      <div className="mx-5 my-3">
        {bill.committeeAgendas.items.map((agenda) => {
          return (
            <div key={`${agenda.agendaId.year}-${agenda.agendaId.number}-${agenda.committeeId.name}`}>
              <Link to={`/agendas/${agenda.agendaId.year}/${agenda.agendaId.number}/${agenda.committeeId.name}`} className="link">
                Committee Agenda #{agenda.agendaId.number} ({agenda.agendaId.year}) - {agenda.committeeId.name}
              </Link>
            </div>
          )
        })}
        {bill.calendars.items.map((cal) => {
          return (
            <div key={`${cal.year}-${cal.calendarNumber}`}>
              <Link to={`/calendars/${cal.year}/${cal.calendarNumber}#${bill.printNo}`} className="link">
                Senate Floor Calendar {cal.calendarNumber} ({cal.year})
              </Link>
            </div>
          )
        })}
      </div>
    </section>
  )
}

function TranscriptReferences({ amendment }) {
  if (amendment.transcripts.size === 0) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Transcript References</h3>
        <div className="mx-5 my-3">
          {amendment.transcripts.items.map((transcript) => {
            const date = new Date(transcript.dateTime);
            const formattedDate = date.toLocaleDateString([], { month: 'long', day: 'numeric', year: 'numeric' });
            const formattedTime = date.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' });
            let href = `/transcripts/session/${transcript.dateTime}/${transcript.sessionType}#bills-${amendment.session}-${amendment.basePrintNo}`;
            if (amendment.version !== '') {
              href += `?amendment=${amendment.version}`
            }
            return (
              <div key={`${transcript.dateTime}-${transcript.sessionType}`}>
                <Link to={href} target="_blank" className="link">
                  {transcript.sessionType} on {formattedDate} at {formattedTime}
                </Link>
              </div>
            )
          })}
        </div>
      </header>
    </section>
  )
}