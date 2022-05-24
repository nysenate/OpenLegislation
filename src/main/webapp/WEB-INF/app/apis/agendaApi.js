import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";
import { PageParams } from "app/shared/Pagination";


export function fetchAgenda(agendaYear, agendaNumber) {
  const url = `/api/3/agendas/${agendaYear}/${agendaNumber}`
  return fetchUrl(url)
}

export function fetchCommitteeAgenda(agendaYear, agendaNumber, committeeName) {
  const url = `/api/3/agendas/${agendaYear}/${agendaNumber}/${committeeName}`
  return fetchUrl(url)
}

export function searchAgendas({ from, to, committee = "", printNo = "", notes = "", sort = "desc", page, limit }) {
  const pageParams = new PageParams(page, limit)
  let term = `(agenda.weekOf:[${from.toISODate()} TO ${to.toISODate()}])`
  if (committee !== "any") {
    term += ` AND (committee.committeeId.name:"${committee}")`
  }
  if (printNo) {
    term += ` AND (committee.addenda.items.bills.items.billId.basePrintNo:"${printNo}")`
  }
  if (notes) {
    term += ` AND (committee.addenda.items.meeting.notes:"${notes}")`
  }

  const url = `/api/3/agendas/search?` + queryString.stringify({
    term: term,
    sort: `agenda.id.number:${sort}`,
    limit: pageParams.limit,
    offset: pageParams.offset,
  })

  return fetchUrl(url)
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
