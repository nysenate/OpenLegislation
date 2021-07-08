import React from "react"
import { DateTime } from "luxon";

/**
 * Inserts a locale full date string
 * @param date An ISO date string.
 * e.g. date of 2021-03-17 will display as March 17, 2021
 */
export default function FullDate({ date, className}) {
 const d = DateTime.fromISO(date)
 return (
   <span className={`${className}`}>{d.toLocaleString(DateTime.DATE_FULL)}</span>
 )
}