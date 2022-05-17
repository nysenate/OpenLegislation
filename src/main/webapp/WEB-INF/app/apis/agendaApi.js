import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";


export function fetchAgenda(agendaYear, agendaNumber) {
  const url = `/api/3/agendas/${agendaYear}/${agendaNumber}`
  return fetchUrl(url)
}

export function fetchCommitteeAgenda(agendaYear, agendaNumber, committeeName) {
  const url = `/api/3/agendas/${agendaYear}/${agendaNumber}/${committeeName}`
  return fetchUrl(url)
}

export function fetchYearlyAgendaList(year) {
  const url = `/api/3/agendas/${year}`
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
