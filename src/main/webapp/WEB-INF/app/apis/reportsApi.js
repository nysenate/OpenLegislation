import "core-js/stable";
import "regenerator-runtime/runtime";

export class SpotcheckType {
  static BOTH = new SpotcheckType("Both")
  static AGENDA = new SpotcheckType("Agenda")
  static CALENDAR = new SpotcheckType("Calendar")
  static types = [SpotcheckType.BOTH, SpotcheckType.AGENDA, SpotcheckType.CALENDAR]

  constructor(name) {
    this.name = name
  }

  static getType = (str) => {
    for (const spotcheckType of SpotcheckType.types) {
      if (spotcheckType.name === str) {
        return spotcheckType
      }
    }
    return null
  }
}

export function runSpotcheck(type, year) {
  const typeStr = type === SpotcheckType.BOTH ? "" : `${type.name}`.toLowerCase()
  return fetchUrl(`/api/3/admin/spotcheck/run/interval/${typeStr}?year=${year}`)
}

export function modifyScrapeQueue(addTo, sessionYear, printNo) {
  const options = {
    method: addTo ? "PUT" : "DELETE"
  }
  return fetchUrl(`/api/3/admin/scraping/billqueue/${sessionYear}/${printNo}`, options);
}

async function fetchUrl(url, options = {}) {
  const response = await fetch(url, options)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}