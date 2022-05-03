import React from 'react'

/**
 * A common Select input component.
 * @param label {string} The label appearing above the select element.
 * @param value {string|number} The starting value for this element.
 * @param options {SelectOption[]} The options for the dropdown.
 * @param onChange {callback} Callback method that is executed when a new selection is made, and takes in an event.
 * @param name {string} Sets the attribute "for" on the label and "id" on the select equal to this value.
 * @param tabIndex {string|number} Used to determine the order of selection when tab is pressed.
 * @param isHighlighted {boolean} If true, the label text background will be highlighted.
 * @param className {string} Classnames to add to the select element.
 * @returns {JSX.Element}
 * @constructor
 */
export default function Select({ label, value, options, onChange, name, tabIndex, isHighlighted = false, className }) {
  let labelClasses = "label label--top"
  labelClasses += isHighlighted ? " bg-yellow-100" : ""

  return (
    <label className={labelClasses} htmlFor={name}>
      {label}
      <select id={name}
              value={value}
              tabIndex={tabIndex}
              onChange={onChange}
              className={`select block ${className}`}>
        {options && options.map((opt) => <option value={opt.value} key={opt.value}>{opt.label}</option>)}
      </select>
    </label>
  )
}

/**
 *
 * @param value {string|number} The model value which represents this option.
 * @param label {string|number} The displayed text for this option.
 */
export function SelectOption(value, label) {
  return {
    label: label,
    value: value
  }
}

/**
 * Common options uses in sort by select elements.
 * @type {{label: (string|number), value: (string|number)}[]}
 */
export const sortOptions = [
  new SelectOption("asc", "Oldest to Newest"),
  new SelectOption("desc", "Newest to Oldest")
]

/**
 * Generates an array on SelectOptions.
 * @param earliestYear to end array with.
 * @param withAny if the array should start with an "any" option.
 * @param onlySessionYears if even years should be skipped.
 * @returns {*[]}
 */
export function yearSortOptions(earliestYear, withAny, onlySessionYears) {
  const currYear = new Date().getFullYear()
  const options = []
  if (withAny) {
    options.push(new SelectOption("", "Any"))
  }
  for (let i = currYear; i >= earliestYear; i--) {
    if (onlySessionYears && i%2 === 0) {
      continue
    }
    options.push(new SelectOption(i, i))
  }
  return options
}
