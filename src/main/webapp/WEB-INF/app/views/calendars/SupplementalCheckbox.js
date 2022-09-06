import React from "react"
import { Checkbox } from "app/shared/Checkbox";
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";


export default function SupplementalCheckboxes({ state, dispatch }) {
  return (
    <div className="my-6 flex items-center gap-x-6 flex-wrap gap-y-3">
      <span className="h5">Supplementals: </span>
      {[ ...state.checkboxes.keys() ].map((key, i) => {
        const box = state.checkboxes.get(key)
        return (
          <span key={i}>
            <Checkbox label={box.label}
                      value={box.isChecked}
                      onChange={e => dispatch({
                        type: "onCheckboxChange",
                        payload: { key: key, checked: e.target.checked }
                      })}
                      name={key} />
          </span>)
      })}
    </div>
  )
}

/**
 * Stores checkbox related information for a single supplemental.
 * @param key {string} The id/key for the supplemental this checkbox represents.
 * @param isChecked {boolean} Is this checkbox checked.
 * @param label {string|jsx} A string or component to use as a label for this checkbox.
 */
function SupplementalCheckbox(key, isChecked, label) {
  this.key = key
  this.isChecked = isChecked
  this.label = label
}

/**
 * Creates a SupplementalCheckbox for each supplemental.
 * @param section
 * @returns {Map<any, any>} Map of supplemental number to SupplementalCheckbox.
 */
export const createCheckboxes = section => {
  let boxes = new Map()
  section.supplementals.forEach(supplemental => {
    boxes.set(
      supplemental.suppId,
      new SupplementalCheckbox(supplemental.suppId, true, createCheckboxLabel(supplemental))
    )
  })
  return boxes
}

const createCheckboxLabel = (supplemental) => {
  let label = ""
  switch (supplemental.suppId) {
    case "0":
    case "ORIGINAL":
      label = "Original"
      break
    default:
      label = `Supplemental ${supplemental.suppId}`
      break
  }
  return (
    <div>
      <h5 className="h5">{label}</h5>
      <span className="text text--small">{formatDateTime(supplemental.releaseDateTime, DateTime.DATETIME_MED)}</span>
    </div>
  )
}