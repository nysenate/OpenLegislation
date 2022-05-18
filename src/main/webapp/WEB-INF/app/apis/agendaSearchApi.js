import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function agendaSearchApi(year, sort, weekOf, agendaNumber, committee, meetingNotes, baseBillNo, limit = 6, offset = 1) {
  let term = ''
  let and = '%20AND%20'
  let yearTerm = '(agenda.id.year:"' + year + '")'
  let agendaNumberTerm = '(agenda.id.number:"' + agendaNumber + '")'
  let committeeTerm = '(committee.committeeId.name:"' + committee + '")'
  let weekOfTerm = '(agenda.weekOf:"'+ weekOf + '")'
  let sortTerm = '(agenda.id.number:"' + sort + '")'

  term = yearTerm

  if (agendaNumber !== 'any') {
    term = term + and + agendaNumberTerm
  }

  if (committee !== 'any') {
    term = term + and + committeeTerm
  }

  if (weekOf !== 'any') {
    term = term + and + weekOfTerm
  }

  if (meetingNotes !== "") {
    term = term + and + "(committee.addenda.items.meeting.notes:" + meetingNotes + ")"
  }

  if (baseBillNo !== "") {
    term = term + and + "(committee.addenda.items.bills.items.billId.basePrintNo:" + baseBillNo + ")"
  }

  term = term + '&sort=' + sortTerm

  const response = await fetch(`/api/3/agendas/search?` + queryString.stringify({
    term: term,
    full: false,
    limit: limit,
    offset: offset,
  }))
  const data = await response.json()
  if (!data.success) {
    console.log('failed to get agenda search api response')
    throw new Error(data.message)
  }
  return data
}