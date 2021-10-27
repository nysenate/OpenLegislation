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
 * @param label The label for the date picker.
 * @param date A luxon DateTime object, not a javascript Date object.
 * @param setDate Callback method which receives a Luxon DateTime representing the selected date time.
 * @param minDate The minimum selectable date.
 * @param maxDate The maximum selectable date.
 * @param name The name for the input element. Defaults to "date"
 * @param className Additional classes for styling the date picker.
 */
export default function DatePicker({ label, date, setDate, minDate, maxDate, name = "date", className }) {
  const jsDate = date?.toJSDate()
  const jsMinDate = minDate?.toJSDate()
  const jsMaxDate = maxDate?.toJSDate()

  // Converts the selected date to a luxon datetime obj before executing the callback function.
  const setDateInternal = (jsDate) => {
    setDate(jsDate === null ? null : DateTime.fromJSDate(jsDate))
  }

  return (
    <label className="label label--top" htmlFor={name}>
      {label}
      <div className={`block ${className}`}>
        <ReactDatePicker
          id={name}
          name={name}
          onChange={(date) => setDateInternal(date)}
          value={jsDate}
          minDate={jsMinDate}
          maxDate={jsMaxDate}
          className={`date-picker ${className}`}
          calendarIcon={<CalendarBlank color="#374151" size="1.2rem" weight="bold" />}
          clearIcon={<X color="#374151" size="1.2rem" weight="bold" />}
        />
      </div>
    </label>
  )
}
