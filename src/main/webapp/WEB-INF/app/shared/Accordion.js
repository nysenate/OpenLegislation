import React from 'react'
import {
  CaretDown,
  CaretUp,
} from "phosphor-react";


export default function Accordion({ title, children, startOpen = false, type = "default" }) {
  return (
    <CoreAccordion title={title} children={children} startOpen={startOpen} type={type} />
  )
}

function CoreAccordion({ title, children, startOpen = false, type = "default" }) {
  const [ isOpen, setIsOpen ] = React.useState(startOpen);
  let headerClass = "flex justify-between items-center p-3 cursor-pointer hover:bg-gray-100";
  headerClass += isOpen ? " bg-gray-100" : ""
  let containerClass = ""

  switch (type) {
    case "default":
      headerClass += " rounded"
      containerClass += " rounded border-1 border-gray-300"
      break
    // Other accordion styles can be created in types here.
    // This "laws" accordion has a thick blue left border to help highlight the accordion data. It's not used anymore.
    // case "laws":
    //   containerClass += isOpen ? " border-l-4 border-blue-400" : " border-l-4 border-transparent"
    //   break
  }

  return (
    <div className={containerClass}>
      <div className={headerClass}
           onClick={() => setIsOpen((open) => !open)}>
        <span className={isOpen ? "text-blue-600 bg-gray-100" : ""}>
        {title}
        </span>
        <div>
          {isOpen
            ? <CaretUp size="1.25rem" weight="bold" className="text-blue-600" />
            : <CaretDown size="1.25rem" weight="bold" />
          }
        </div>
      </div>

      {isOpen &&
        <div className="w-full">
          {children}
        </div>
      }
    </div>
  )
}
