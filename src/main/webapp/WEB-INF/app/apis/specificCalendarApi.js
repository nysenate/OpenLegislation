import "core-js/stable";
import "regenerator-runtime/runtime";

export default async function calendarSearchApi(year, number) {
  const response = await fetch(`/api/3/calendars/` + year + `/` + number)
  const data = await response.json()
  if (!data.success) {
    console.log('failed to get specific calendar api response')
    throw new Error(data.message)
  }
  return data
}