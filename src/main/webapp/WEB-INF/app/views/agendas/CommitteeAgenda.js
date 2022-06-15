import React from "react"
import {
  Link,
  Redirect,
  useParams
} from "react-router-dom";
import { fetchCommitteeAgenda } from "app/apis/agendaApi";
import LoadingIndicator from "app/shared/LoadingIndicator";
import {
  capitalize,
  capitalizePhrase
} from "app/lib/textUtils";
import {
  DATETIME_FULL_WITH_WEEKDAY,
  formatDateTime,
  MONTH_AND_DAY
} from "app/lib/dateUtils";
import Tabs from "app/shared/Tabs";
import MemberListing from "app/shared/MemberListing";
import TruncatedList from "app/shared/TruncatedList";
import MemberThumbnail from "app/shared/MemberThumbnail";
import { DateTime } from "luxon";
import BillStatusDesc from "app/shared/BillStatusDesc";
import BillMilestones from "app/shared/BillMilestones";


const registerReducer = function (state, action) {
  switch (action.type) {
    case "loading":
      return {
        ...state,
        isLoading: true,
        agenda: {},
        committeeAgenda: {},
        errorMsg: "",
        tabs: [],
        activeTab: "",
      }
    case "success":
      return {
        ...state,
        isLoading: false,
        agenda: action.payload.result.agenda,
        committeeAgenda: action.payload.result.committee,
        activeTab: getAddendumName(action.payload.result.committee.addenda.items[0]),
        tabs: createTabs(action.payload.result.committee),
      }
    case "error":
      return {
        ...state,
        isLoading: false,
        errorMsg: action.payload.message,
      }
    case "onTabChange":
      return {
        ...state,
        activeTab: action.payload,
      }
    default:
      return state
  }
}

const initialState = {
  agenda: {},
  committeeAgenda: {},
  isLoading: true,
  errorMsg: "",
  tabs: [],
  activeTab: "",
}

export default function CommitteeAgenda({ setHeaderText }) {
  const { agendaYear, agendaNumber, committee } = useParams()
  const [ state, dispatch ] = React.useReducer(registerReducer, initialState)

  React.useEffect(() => {
    dispatch({ type: "loading" })
    fetchCommitteeAgenda(agendaYear, agendaNumber, committee)
      .then(res => dispatch({ type: "success", payload: res }))
      .catch(err => dispatch({ type: "error", payload: err }))
  }, [ agendaYear, agendaNumber, committee ])

  React.useEffect(() => {
    const headerText = state.agenda.id
      ? `Agenda ${state.agenda?.id?.number} - ${capitalize(state.committeeAgenda?.committeeId?.name)}`
      : "Agenda"
    setHeaderText(headerText)
  }, [ state.agenda, state.committeeAgenda ])

  if (state.isLoading) {
    return <div className="m-3"><LoadingIndicator /></div>
  }

  if (!state.isLoading && state.errorMsg) {
    return <Redirect to="/404" />
  }

  return (
    <div className="">
      <div className="p-3">
        <h3 className="h3">
          {capitalize(state.committeeAgenda.committeeId.name)} committee agenda for the week
          of {formatDateTime(state.agenda.weekOf, MONTH_AND_DAY)}.
        </h3>
      </div>
      <div className="my-1">
        <Tabs tabs={state.tabs}
              activeTab={state.activeTab}
              setActiveTab={(selectedTabName) => dispatch({ type: "onTabChange", payload: selectedTabName })} />
      </div>
    </div>
  )
}

function CommitteeAgendaAddendum({ addendum }) {
  return (
    <div className="p-3 mt-4">
      <div>
        <ul>
          <li>
            <span className="font-semibold">Meeting Date:</span> {formatDateTime(addendum.meeting.meetingDateTime, DATETIME_FULL_WITH_WEEKDAY)}
          </li>
          <li>
            <span className="font-semibold">Location:</span> {addendum.meeting.location}
          </li>
          <li>
            <span className="font-semibold">Chair:</span> {addendum.meeting.chair}
          </li>
          {hasNotes(addendum.meeting.notes) &&
            <li>
              <span className="font-semibold">Notes:</span> {addendum.meeting.notes}
            </li>
          }
        </ul>
      </div>
      {addendum.hasVotes &&
        <div className="my-6">
          <VotingAttendance addendum={addendum} />
        </div>
      }
      <div className="my-10">
        <BillsOnAddendum addendum={addendum} />
      </div>
    </div>
  )
}

const createTabs = committeeAgenda => {
  return committeeAgenda.addenda.items.map(addendum => {
    return {
      name: getAddendumName(addendum),
      isDisable: false,
      component: <CommitteeAgendaAddendum addendum={addendum} />
    }
  })
}

const getAddendumName = addendum => {
  return addendum.addendumId === "" ? "Initial Addendum" : `Addendum ${addendum.addendumId}`
}

/**
 * A note of just "\n" is considered empty.
 */
const hasNotes = (notes) => {
  return notes && notes !== "\n"
}


function VotingAttendance({ addendum }) {
  const attendanceList = addendum.voteInfo?.attendanceList?.items.map(memberAttendance =>
    <MemberAttendanceListing memberAttendance={memberAttendance} />
  )

  return (
    <div>
      <h4 className="h4 mb-1">Voting Attendance ({addendum.voteInfo.attendanceList.size})</h4>
      <TruncatedList list={attendanceList} />
    </div>
  )
}

function MemberAttendanceListing({ memberAttendance }) {
  return (
    <div className="flex items-center gap-x-3">
      <MemberListing member={memberAttendance.member} />
      <div>
        ({memberAttendance.party}) - {memberAttendance.attend}
      </div>
    </div>
  )
}

function BillsOnAddendum({ addendum }) {
  if (addendum.bills.size < 1) {
    return null
  }
  return (
    <div>
      <h4 className="h4 mb-3">Bills added to the agenda</h4>
      <div>
        {addendum.bills.items.map(bill =>
          <BillVoteListing billInfo={bill.billInfo}
                           to={`/bills/${bill.billInfo.session}/${bill.billInfo.basePrintNo}?tab=Votes`}
                           voteInfo={getVoteInfo(bill, addendum)}
                           key={bill.billInfo.basePrintNoStr} />
        )}
      </div>
    </div>
  )
}

const getVoteInfo = (bill, addendum) => {
  const billVote = addendum.voteInfo?.votesList?.items.find((billVote) =>
    billVote.bill.basePrintNoStr === bill.billInfo.basePrintNoStr)
  if (!billVote) {
    return null
  }
  return {
    action: capitalizePhrase(billVote.action.replaceAll("_", " ")),
    aye: billVote.vote.memberVotes.items.AYE,
    nay: billVote.vote.memberVotes.items.NAY,
    exc: billVote.vote.memberVotes.items.EXC,
    abs: billVote.vote.memberVotes.items.ABS,
    abd: billVote.vote.memberVotes.items.ABD,
    ayewr: billVote.vote.memberVotes.items.AYEWR,
  }
}

function BillVoteListing({ billInfo, to, voteInfo }) {
  const { aye, nay, exc, abs, abd, ayewr, action } = voteInfo || {}
  if (!billInfo) {
    return null
  }
  return (
    <Link to={to}>
      <div className="p-3 flex flex-wrap hover:bg-gray-200 rounded">
        <div className="flex items-center w-full md:w-3/12">
          <MemberThumbnail member={billInfo?.sponsor?.member} />
          <div>
            <div className="text">
              {billInfo.basePrintNo}-{billInfo.session}
            </div>
            <div className="text text--small">
              {billInfo.sponsor && billInfo.sponsor.member &&
                billInfo.sponsor.member.fullName}
            </div>
          </div>
        </div>

        <div className="w-full md:w-4/12 pr-10">
          <h5 className="text border-b-1 my-1">Votes</h5>
          {voteInfo &&
            <div>
              <div className="flex flex-wrap">
                {aye && <VoteCount type="AYE" count={aye.size} />}
                {nay && <VoteCount type="NAY" count={nay.size} />}
                {exc && <VoteCount type="EXC" count={exc.size} />}
                {abs && <VoteCount type="ABS" count={abs.size} />}
                {abd && <VoteCount type="ABD" count={abd.size} />}
                {ayewr && <VoteCount type="AYEWR" count={ayewr.size} />}
              </div>
              <div className="ml-1">
                <span className="text text--small font-semibold">Action:</span>
                <span className="text text--small"> {voteInfo.action}</span>
              </div>
            </div>
          }
          {!voteInfo &&
            <div className="text text--small"> No votes</div>
          }
        </div>

        <div className="w-full md:w-5/12 mt-2 md:mt-0">
          <div className="text text--small">
            <span>{billInfo.title}</span>
          </div>
          {billInfo.status.actionDate &&
            <div className="mt-1 text text-blue-600">
              {formatDateTime(billInfo.status.actionDate, DateTime.DATE_FULL)} - <BillStatusDesc status={billInfo.status} />
            </div>
          }
          {!billInfo.billType.resolution &&
            <BillMilestones milestones={billInfo.milestones.items}
                            chamber={billInfo.billType.chamber}
                            className="py-3" />
          }
        </div>
      </div>
    </Link>
  )
}

function VoteCount({ type, count }) {
  let colorClasses = ""
  switch (type) {
    case "AYE": // fall through
    case "AYEWR":
      colorClasses = "bg-green-100 border-1 border-green-200"
      break
    case "NAY":
      colorClasses = "bg-red-100 border-1 border-red-200"
      break
    default:
      colorClasses = "bg-gray-100 border-1 border-gray-200"
      break
  }
  return (
    <span className={`text text--small p-1 m-1 rounded ${colorClasses}`}>
      {type} ({count})
    </span>
  )
}
