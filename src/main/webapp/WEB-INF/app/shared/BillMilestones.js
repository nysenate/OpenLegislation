import React, { useEffect } from "react"
import Tippy from "@tippyjs/react";
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";

const createMilestone = (desc) => {
  return {
    statusDesc: desc,
    actionDate: null
  }
}

const senateMilestones = [
  createMilestone("In Senate Committee"),
  createMilestone("Senate Floor Calendar"),
  createMilestone("Passed Senate"),
]

const assemblyMilestones = [
  createMilestone("In Assembly Committee"),
  createMilestone("Assembly Floor Calendar"),
  createMilestone("Passed Assembly"),
]

const otherMilestones = [
  createMilestone("Delivered to Governor"),
  createMilestone("Signed by Governor"),
]

/**
 * Create default milestones which are ordered differently depending on the chamber.
 * Returned object fields are milestone statusDesc's with values equal to the milestone. This
 * allows us to easily replace the default milestones with bill milestones.
 * @param chamber
 * @returns {*}
 */
function defaultMilestones(chamber) {
  switch(chamber) {
    case "SENATE":
      return  senateMilestones.concat(assemblyMilestones).concat(otherMilestones).reduce(reducer, {})
    default:
      return  assemblyMilestones.concat(senateMilestones).concat(otherMilestones).reduce(reducer, {})
  }

  function reducer(accum, curr) {
    return {...accum, [curr.statusDesc]: curr}
  }
}

export default function BillMilestones({ milestones, chamber, className }) {

  // The union of milestones and defaultMilestones.
  const [allMilestones, setAllMilestones] = React.useState({})

  React.useEffect(() => {
    let ms = defaultMilestones(chamber);
    milestones.forEach(m => {
      ms[m.statusDesc] = m
    })

    setAllMilestones(ms);
  }, [milestones, chamber])

  return (
    <div className={`w-72 flex justify-around items-center ${className}`}>
      {Object.entries(allMilestones).map(([key, value], index) => {
        return (
          <React.Fragment key={value.statusDesc}>
            {index ? <Line /> : ""}
            <Milestone milestone={value} />
          </React.Fragment>
        )
      })}
    </div>
  )
}

function Milestone({ milestone }) {
  let className = "h-3 w-3 rounded-full border-1 pointer"
  if (milestone.statusDesc === 'Vetoed') {
    className += " bg-red-500 border-red-500"
  } else if (milestone.actionDate !== null) {
    className += " bg-blue-500"
  } else {
    className += " border-blue-500"
  }

  return (
    <Tippy content={<MilestoneTooltipText milestone={milestone} />}
           placement="bottom"
           maxWidth="28rem"
           className="py-1 px-2 tooltip tooltip__text">
      <div className={className}>
      </div>
    </Tippy>
  )
}

function MilestoneTooltipText({ milestone }) {
  return (
    <div>
      <span>{milestone.statusDesc}</span>
      {milestone.actionDate &&
      <span> | {formatDateTime(milestone.actionDate, DateTime.DATE_FULL)}</span>
      }
      {milestone.committeeName &&
      <span> | {milestone.committeeName}</span>
      }
      {milestone.billCalNo &&
      <span> | Cal #{milestone.billCalNo}</span>
      }
    </div>
  )
}

function Line() {
  return (
    <div className="h-0 border-b-1 border-blue-500 border-dashed flex-grow">
    </div>
  )
}