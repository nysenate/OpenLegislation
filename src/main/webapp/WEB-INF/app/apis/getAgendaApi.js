import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function getAgendaApi( agendaYear, agendaNumber) {
  const response = await fetch(`/api/3/agendas/` + agendaYear + '/' + agendaNumber )
  const data = await response.json()
  if (!data.success) {
    console.log('failed to get agenda api response')
    throw new Error(data.message)
  }
  return data
}