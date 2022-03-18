import {
  ArrowUUpLeft,
  CaretLeft,
  CaretRight
} from "phosphor-react";
import { Link } from "react-router-dom";
import React from "react";


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
      <Link to={to} className="border-0">
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
  NavigationLink,
  Spacer,
}
