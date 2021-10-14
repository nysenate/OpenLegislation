import React from 'react'

export const ASC = "asc"
export const DESC = "desc"

/**
 * A common select element for sorting by ascending or descending.
 * @param sort The current sort value, should reference ASC or DESC constants in this file.
 * @param onChange Callback which is given the new value on change.
 * @param name The name of the element, defaults to "sort".
 * @param className Additional classes for styling the select.
 */
export default function SortBy({sort = DESC, onChange, name = "sort", className}) {

  return (
    <select className={`select ${className}`}
            value={sort}
            onChange={(e) => onChange(e.target.value)}
            name={name}>
      <option value={ASC}>Oldest to Newest</option>
      <option value={DESC}>Newest to Oldest</option>
    </select>
  )
}
