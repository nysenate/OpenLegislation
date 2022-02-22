import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function getCalendarApi( calendarYear, calendarNumber) {
  const response = await fetch(`/api/3/calendars/` + calendarYear + '/' + calendarNumber )
  const data = await response.json()
  if (!data.success) {
    console.log('failed to get calendar api response')
    throw new Error(data.message)
  }
  return data
}