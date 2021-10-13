import React from 'react'
import { default as ReactDatePicker } from 'react-date-picker'
import {
  CalendarBlank,
  X
} from "phosphor-react";
import { DateTime } from "luxon";

/**
 * A common date picker component to be used by OL React.
 * Dates sent to and received from this component will be luxon DateTime objects, not JS Date objects.
 * @param date A luxon DateTime object, not a javascript Date object.
 * @param setDate Callback method which receives a Luxon DateTime representing the selected date time.
 * @param minDate The minimum selectable date.
 * @param maxDate The maximum selectable date.
 */
export default function DatePicker({ date, setDate, minDate, maxDate }) {
  const jsDate = date?.toJSDate()
  const jsMinDate = minDate?.toJSDate()
  const jsMaxDate = maxDate?.toJSDate()

  // Converts the selected date to a luxon datetime obj before executing the callback function.
  const setDateInternal = (jsDate) => {
    setDate(jsDate === null ? null : DateTime.fromJSDate(jsDate))
  }

  return (
    <ReactDatePicker
      onChange={(date) => setDateInternal(date)}
      value={jsDate}
      minDate={jsMinDate}
      maxDate={jsMaxDate}
      className="date-picker"
      calendarIcon={<CalendarBlank color="#374151" size="1.2rem" weight="bold" />}
      clearIcon={<X color="#374151" size="1.2rem" weight="bold" />}
    />
  )
}
