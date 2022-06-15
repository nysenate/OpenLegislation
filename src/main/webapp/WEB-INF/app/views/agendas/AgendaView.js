import React from 'react';
import {
  Link,
  Redirect,
  useParams
} from "react-router-dom";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { fetchAgenda } from "app/apis/agendaApi";
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import {
  CalendarBlank,
  Info
} from "phosphor-react";
import Note from "app/shared/Note";


export default function AgendaView({ setHeaderText }) {
  const [ agenda, setAgenda ] = React.useState()
  const [ isLoading, setIsLoading ] = React.useState(true)
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const { agendaYear, agendaNumber } = useParams()

  React.useEffect(() => {
    getAgenda(agendaYear, agendaNumber)
  }, [ agendaYear, agendaNumber ])

  React.useEffect(() => {
    if (agenda) {
      setHeaderText(`Agenda ${agenda?.id?.number},  ${agenda?.id?.year}`)
    }
  }, [ agenda ])

  const getAgenda = (agendaYear, agendaNumber) => {
    setIsLoading(true)
    fetchAgenda(agendaYear, agendaNumber)
      .then(res => setAgenda(res.result))
      .catch(error => setErrorMsg(error.message))
      .finally(() => setIsLoading(false))
  }

  if (isLoading) {
    return (
      <div className="m-3">
        <LoadingIndicator />
      </div>
    )
  }

  if (!isLoading && errorMsg) {
    return <Redirect to="/404" />
  }

  return (
    <div>
      <div className="p-3 border-b-8 border-gray-100">
        <AgendaSummary agenda={agenda} />
      </div>
      <div className="px-3">
        <div className="mt-3 mb-6">
          <AgendaNote />
        </div>

        <div className="pb-3">
          <AgendaCommitteeList committeeAgendas={agenda.committeeAgendas.items} />
        </div>
      </div>
    </div>
  )
}

function AgendaSummary({ agenda }) {
  if (!agenda) {
    return null
  }

  return (
    <div className="px-1 py-3 md:px-3 flex items-center justify-between gap-x-3 flex-wrap">
      <div className="w-full mb-3 md:w-auto md:mb-0">
        <div className="flex items-center">
          <CalendarBlank size="1.4rem" weight="bold" />&nbsp;
          <span className="h4">Week of {formatDateTime(agenda.weekOf, DateTime.DATE_FULL)}</span>
        </div>
        <div className="text text--small">
          Published on {formatDateTime(agenda.publishedDateTime, DateTime.DATETIME_SHORT)}
        </div>
      </div>
      <SummaryItem count={agenda.totalAddendum} label="Addenda" />
      <SummaryItem count={agenda.totalCommittees} label="Committee(s)" />
      <SummaryItem count={agenda.totalBillsConsidered} label="Bills on Agenda" />
      <SummaryItem count={agenda.totalBillsVotedOn} label="Bills Voted On" />
    </div>
  )
}

function SummaryItem({ count, label }) {
  return (
    <div className="text-center">
      <div className="h4">{count}</div>
      <div>{label}</div>
    </div>
  )
}

export function SummaryItemSmall({ count, label }) {
  return (
    <div className="text-center">
      <div className="font-semibold">{count}</div>
      <div className="text text--small">{label}</div>
    </div>
  )
}

function AgendaNote() {
  return (
    <Note>
      <div className="flex items-center px-3">
        <Info size="1.6rem" />
        <span className="text text--small italic">&nbsp;A committee may receive multiple updates (i.e. addenda)
          which can either overwrite prior meeting details or supplement them.</span>
      </div>
    </Note>
  )
}

function AgendaCommitteeList({ committeeAgendas }) {
  return (
    <div>
      {committeeAgendas.map((ca, index, row) => (
        <div key={ca.committeeId.name}>
          <AgendaCommitteeSummaryLink committeeAgenda={ca} />
          {index < (row.length - 1) &&
            <hr />
          }
        </div>
      ))}
    </div>
  )
}

function AgendaCommitteeSummaryLink({ committeeAgenda }) {
  return (
    <Link to={location => `${location.pathname}/${committeeAgenda.committeeId.name}`}>
      <div className="p-3 hover:bg-gray-200 rounded flex items-center justify-between gap-x-3 flex-wrap">
        <div className="flex-0 w-full md:w-1/3 mb-2 md:mb-0">
          <span className="text-sm lg:text-base font-semibold link">
          {committeeAgenda.committeeId.name}
          </span>
        </div>
        <div className="flex-1">
          <SummaryItemSmall count={committeeAgenda.addenda.size} label="Addenda" />
        </div>
        <div className="flex-1">
          <SummaryItemSmall count={countAddendaBills(committeeAgenda)} label="Bills on Agenda" />
        </div>
        <div className="flex-1">
          <SummaryItemSmall count={countVotedBills(committeeAgenda)} label="Bills Voted On" />
        </div>
      </div>
    </Link>
  )
}

const countAddendaBills = committeeAgenda => {
  return committeeAgenda.addenda.items.reduce((prev, curr) => prev + curr.bills.size, 0)
}

const countVotedBills = committeeAgenda => {
  return committeeAgenda.addenda.items.reduce((prev, curr) => prev + (curr.voteInfo?.votesList?.size || 0), 0)
}
