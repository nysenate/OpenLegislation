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

/**
 * Converts an ISO date or datetime into the specified format.
 * @param dateString A String representing an ISO date time.
 * @param format The format to convert to - a format object from luxon,
 *               see https://moment.github.io/luxon/#/formatting?id=presets
 * @returns {string}
 */
export function formatDateTime(dateString, format) {
  return DateTime.fromISO(dateString).toLocaleString(format)
}

/**
 * Example of this format: October 14, 1983, 1:30 PM
 */
export const DATETIME_FULL_NO_ZONE = {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  hour: 'numeric',
  minute: '2-digit',
}

export const DATETIME_FULL_WITH_WEEKDAY = {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long',
  hour: 'numeric',
  minute: '2-digit',
}

/**
 * Example of this format: October 14
 */
export const MONTH_AND_DAY = {
  month: 'long',
  day: 'numeric',
}