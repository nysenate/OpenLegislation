import React from 'react'
import BillListing from "app/shared/BillListing";
import Accordion from "app/shared/Accordion";
import MemberListing from "app/shared/MemberListing";

export default function SpecificAgendaCommittee({ response }) {

  if (response.success === false) {
    return (
      <div>
        No results found
      </div>
    )
  }

  const committees = response.items
  // console.log(committees)

  return (
    // <div>committees will go here</div>
    <div className="mt-8">
      <div className="pt-3">

        {committees.map((r, index) =>
            <Committee response={r} key={index}/>
        )}
      </div>
    </div>
  )
}

function Committee({response}) {

  // console.log(response)

  let committeeId = response.committeeId
  let addenda = response.addenda.items

  // console.log(committeeId)
  // console.log(addenda)

  return (
    <div>
      {addenda.map((r, index) =>
          <Addenda committeeId={committeeId} addenda={r} key={index}/>
      )}
    </div>
  )


}

function Addenda({committeeId, addenda}) {

  let bills = addenda.bills
  let voteInfo = addenda.voteInfo
  let meeting = addenda.meeting
  let committeeAgendaAddendumId = addenda.committeeAgendaAddendumId

  return (
    <div>
      <Accordion title={committeeId.name} startOpen={true}>
        <BillsList bills={ bills } />
        <MeetingList meeting={ meeting } voteInfo={ voteInfo } committeeAgendaAddendumId={committeeAgendaAddendumId}/>
      </Accordion>
    </div>
  )
}

function MeetingList({ meeting, voteInfo, committeeAgendaAddendumId }) {
  let attendanceArray = voteInfo.attendanceList.items

  console.log(attendanceArray)
  console.log(meeting)
  console.log(voteInfo)
  console.log(committeeAgendaAddendumId)

  return (
    <div>
      <Accordion title={committeeAgendaAddendumId.committeeId.name + " Meeting Information"} startOpen={false}>
        <h2>{committeeAgendaAddendumId.addendum}</h2>
        <div>
          <p>Meeting Date Time: {meeting.meetingDateTime}</p>
          <p>Location: {meeting.location}</p>
          <p>Chair: {meeting.chair}</p>
          <p>Notes: {meeting.notes}</p>
        </div>

        <div>
          <h2>Voting Attendance</h2>

          {attendanceArray.map((r, index) =>
            <div key={index}>
              <MemberListing member={r.member} />
              <p>({r.party}) - {r.aattend}</p>
            </div>
          )}

        </div>

      </Accordion>
    </div>
  )
}

function BillsList({ bills }) {

  // console.log(bills)

  return (
    <div>
      <Accordion title={"Bills added to the Agenda " + bills.items.length} startOpen={false}>
        {bills.items.map((r) =>
          <BillListing bill={r.billInfo}
                       highlights={r.billInfo.title}
                       to={`/bills/${r.billInfo.session}/${r.billInfo.basePrintNo}`}
                       key={r.billInfo.basePrintNoStr} />
        )}
      </Accordion>
    </div>
  )
}


