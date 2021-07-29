import { DateTime } from "luxon";

/**
 * Convert a year into its session year.
 * @param year The year to convert, defaults to the current year.
 */
export function sessionYear(year = new Date().getFullYear()) {
  return (year % 2 === 0) ? year - 1 : year
}

export function billSessionYears() {
  const earliestBillSession = 2009;
  const sessionYears = [];
  for (let session = sessionYear(); session >= earliestBillSession; session -= 2) {
    sessionYears.push(session);
  }
  return sessionYears;
}
