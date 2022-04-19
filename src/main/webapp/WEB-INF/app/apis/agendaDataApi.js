import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function agendaDataApi(year, limit = 100, offset = 0) {
  // /api/3/agendas/2022?sort=&limit=100&offset=
  // console.log(`/api/3/agendas/` + year + `?` +queryString.stringify({
  //   limit: limit,
  //   offset: offset,
  // }))
  const response = await fetch(`/api/3/agendas/` + year + `?` + queryString.stringify({
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