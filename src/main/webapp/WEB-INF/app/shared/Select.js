import React from 'react'

/**
 * A common Select input component.
 * @param label {string} The label for this select.
 * @param value {string|number} The value for this select.
 * @param options {SelectOption[]} The options for this select.
 * @param onChange {callback} Callback method executed when a new selection is made, it is given the event.
 * @param name {string} Sets the attribute "for" on the label and "id" on the select equal to this value.
 * @param tabIndex {string|number} The tab index.
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
