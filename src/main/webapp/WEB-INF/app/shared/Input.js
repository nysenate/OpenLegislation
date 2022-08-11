import React from 'react'


/**
 * A common text input component.
 * @param label {string} The input's label.
 * @param value {string|number} The input's value.
 * @param onChange {callback} Callback method executed when input is changed, it is given the event.
 * @param placeholder {string} The input's placeholder text.
 * @param type {string} The type of input, this should support all string based types, i.e. text, email, tel, etc. Defaults to text.
 * @param name {string} Sets the attribute "for" on the label and "id" on the input equal to this value.
 * @param tabIndex {string|number} The input's tab index.
 * @param isHighlighted {boolean} If true, the label text background will be highlighted.
 * @param className {string} Classnames to add to the input.
 * @returns {JSX.Element}
 * @constructor
 */
export default function Input({
                                label,
                                value,
                                onChange,
                                placeholder,
                                type = "text",
                                name,
                                tabIndex,
                                isHighlighted = false,
                                className,
                                ...params
                              }) {
  let labelClasses = "label label--top"
  labelClasses += isHighlighted ? " bg-yellow-100" : ""

  return (
    <label className={labelClasses} htmlFor={name}>
      {label}
      <input id={name}
             value={value}
             onChange={onChange}
             type={type}
             placeholder={placeholder}
             tabIndex={tabIndex}
             className={`input block ${className}`}
             {...params}
      />
    </label>
  )

}
