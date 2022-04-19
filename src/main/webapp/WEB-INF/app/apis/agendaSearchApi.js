import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function agendaSearchApi(year, sort, weekOf, agendaNumber, committee, limit = 6, offset = 1) {
  //?term=(agenda.id.year:"2022")&sort=agenda.id.number:desc&limit=6&offset=1
  //term=(agenda.id.year:%222022%22)%20AND%20(agenda.id.number:%222%22)%20AND%20(committee.committeeId.name:%22Aging%22)%20AND%20(agenda.weekOf:%222022-01-10%22)&sort=agenda.id.number:desc&limit=6&offset=1
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

  term = term + '&sort=' + sortTerm

  // console.log(term)
  // console.log(`/api/3/agendas/search?` + queryString.stringify({
  //   term: term,
  //   full: false,
  //   limit: limit,
  //   offset: offset,
  // }))
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