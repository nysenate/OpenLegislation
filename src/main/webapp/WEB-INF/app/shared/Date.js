import React from "react"
import { DateTime } from "luxon";

/**
 * Converts a ISO date string into the specified format.
 * @param date An ISO date string.
 * @param format A luxon date time format. See https://moment.github.io/luxon/#/formatting?id=presets
 */
export default function Date({ date, format, className}) {
 const d = DateTime.fromISO(date)
 return (
   <span className={`${className}`}>{d.toLocaleString(format)}</span>
 )
}