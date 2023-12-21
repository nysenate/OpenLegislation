import React from 'react'
import { CalendarBlank, } from "phosphor-react";
import { DateTime } from "luxon";
import ReactDatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";

/**
 * A common date picker component to be used by OL React.
 * Dates sent to and received from this component will be luxon DateTime objects, not JS Date objects.
 *
 * This uses the react-datepicker library which can be found at https://reactdatepicker.com/.
 *
 * @param label The label for the date picker.
 * @param name The value to use for the input's "name" and label's "for" attributes.
 * @param date A luxon DateTime object representing the initial date.
 * @param setDate Callback method which receives a Luxon DateTime representing the selected date time.
 * @param minDate Optional - The minimum selectable date. A Luxon DateTime object.
 * @param maxDate Optional - The maximum selectable date. A Luxon DateTime object.
 * @param className Optional - Additional classes for styling the date picker.
 * @param rest Any additional configuration necessary for an individual date picker.
 *             - See https://reactdatepicker.com/ for options.
 *             - Useful Examples:
 *                  - {@code selectsStart} and {@code selectsEnd} When using 2 datepickers to specify a date range
 *                      these attributes indicate which datepicker is the start and end of the range.
 *                  - {@code showTimeSelect} attribute will add the ability to select the time.
 */
export default function DatePicker({ label, name, date, setDate, minDate, maxDate, className, ...rest }) {
  return (
    <div>
      <label className="label label--top" htmlFor={name}>
        {label} <CalendarBlank color="#374151" size="1.2rem" className="inline" />
      </label>
      <ReactDatePicker
        selected={date?.toJSDate()}
        onChange={(date) => setDate(DateTime.fromJSDate(date))}
        minDate={minDate?.toJSDate() || defaultMinDate}
        maxDate={maxDate?.toJSDate()}
        name={name}
        todayButton="Today"
        showYearDropdown
        showMonthDropdown
        dropdownMode="select"
        className={`date-picker ${className ? className : ""}`}
        {...rest} />
    </div>
  )
}

// If no min date is specified, use the start of OL data in 2009.
const defaultMinDate = DateTime.fromFormat("2009-01-01", "yyyy-MM-dd").toJSDate()
