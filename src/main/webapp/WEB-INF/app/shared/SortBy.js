import React from 'react'

export const ASC = "asc"
export const DESC = "desc"

/**
 * A common select element for sorting by ascending or descending.
 * @param sort The current sort value, should reference ASC or DESC constants in this file.
 * @param onChange Callback which is given the new value on change.
 * @param name The name of the element, defaults to "sort".
 */
export default function SortBy({sort = DESC, onChange, name = "sort"}) {

  return (
    <select className="select"
            value={sort}
            onChange={(e) => onChange(e.target.value)}
            name="sort">
      <option value={ASC}>Oldest to Newest</option>
      <option value={DESC}>Newest to Oldest</option>
    </select>
  )
}
