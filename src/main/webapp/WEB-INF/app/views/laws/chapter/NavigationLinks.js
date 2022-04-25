import {
  ArrowUUpLeft,
  CaretLeft,
  CaretRight
} from "phosphor-react";
import { Link } from "react-router-dom";
import React from "react";
import { capitalize } from "app/lib/textUtils";


function LawNavigationBar({ node, docType }) {
  if (!node) {
    return null
  }

  // The node.parents array contains all parents including grandparents, etc.
  // The last element in the parents array is this node's parent.
  const [ parent ] = node.parents?.slice(-1) || []
  let prevLink
  let parentLink
  let nextLink

  switch (docType) {
    case "CHAPTER":
      parentLink = <NavigationLink type="up"
                                   label="Back to Law Search"
                                   to="/laws" />
      break
    case "SECTION":
      node.prevSibling
        ? prevLink = <NavigationLink type="prev"
                                     label={`Previous ${capitalize(node.prevSibling.docType)}`}
                                     to={`/laws/${node.lawId}/leaf/${node.prevSibling.locationId}`} />
        : <Spacer />

      parent
        ? parentLink = <NavigationLink type="up"
                                       label={`Back to ${capitalize(parent.docType)}`}
                                       to={`/laws/${node.lawId}/node/${parent.locationId}`} />
        : <Spacer />

      node.nextSibling
        ? nextLink = <NavigationLink type="next"
                                     label={`Next ${capitalize(node.nextSibling.docType)}`}
                                     to={`/laws/${node.lawId}/leaf/${node.nextSibling.locationId}`} />
        : <Spacer />
      break
    default: // Articles, Titles, etc
      node.prevSibling
        ? prevLink = <NavigationLink type="prev"
                                     label={`Previous ${capitalize(node.prevSibling.docType)}`}
                                     to={`/laws/${node.lawId}/node/${node.prevSibling.locationId}`} />
        : <Spacer />

      parent
        ? parentLink = <NavigationLink type="up"
                                       label={`Back to ${capitalize(parent.docType)}`}
                                       to={parent.docType === "CHAPTER"
                                         ? `/laws/${parent.lawId}`
                                         : `/laws/${parent.lawId}/node/${parent.locationId}`} />
        : <Spacer />

      node.nextSibling
        ? nextLink = <NavigationLink type="next"
                                     label={`Next ${capitalize(node.nextSibling.docType)}`}
                                     to={`/laws/${node.lawId}/node/${node.nextSibling.locationId}`} />
        : <Spacer />
      break
  }

  return (
    <div className="grid grid-cols-3">
      {prevLink || <Spacer />}
      {parentLink || <Spacer />}
      {nextLink || <Spacer />}
    </div>
  )
}

function Spacer() {
  return <div />
}

function NavigationLink({ label, type, to }) {
  let icon
  let header
  let containerClass = "flex"
  switch (type) {
    case "up":
      header = "Back"
      containerClass += " justify-center"
      icon = <ArrowUUpLeft size="1.5rem" weight="bold" className="mr-1" />
      break
    case "next":
      header = "Next"
      containerClass += " justify-end"
      icon = <CaretRight size="1.5rem" weight="bold" className="mr-1" />
      break
    case "prev":
      header = "Previous"
      containerClass += " justify-start"
      icon = <CaretLeft size="1.5rem" weight="bold" className="mr-1" />
      break
  }

  return (
    <div className={containerClass}>
      <Link to={to} className="border-0" class="link border-0">
        <div className="flex items-center p-3 hover:bg-gray-200 rounded">
          {type === "next"
            ? <>{label} {icon}</>
            : <>{icon} {label}</>
          }
        </div>
      </Link>
    </div>
  )
}

export {
  LawNavigationBar,
}
