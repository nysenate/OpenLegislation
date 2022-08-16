import React from "react"


/**
 * @param label {string} The label appearing besides the checkbox.
 * @param value {boolean} The value of this checkbox
 * @param onChange {callback} Callback method that is executed when a change is made, it is given the event as its only parameter.
 * @param name {string} The value for the id, name, and htmlFor attributes.
 * @returns {JSX.Element}
 * @constructor
 */
export function Checkbox({ label, value, onChange, name }) {
  return (
    <div className="flex items-center">
      <input type="checkbox"
             id={name}
             name={name}
             checked={value}
             onChange={onChange}
             className="cursor-pointer" />
      <label htmlFor={name} className="label ml-1 cursor-pointer">
        {label}
      </label>
    </div>
  )
}

/**
 * Similar to Checkbox, except this adds a spacer to make it align well with Select and Input components.
 */
export function FormCheckbox({ label, value, onChange, name }) {
  return (
    <div>
      <div className="h-7"></div>
      <Checkbox label={label} value={value} onChange={onChange} name={name} />
    </div>
  )
}
