import React from 'react'
import {
  CaretDown,
  CaretUp,
} from "phosphor-react";


export default function Accordion({ title, children }) {
  const [ isOpen, setIsOpen ] = React.useState(false);
  let headerClass = "flex justify-between items-center p-3 rounded cursor-pointer hover:bg-gray-100";
  headerClass += isOpen ? " bg-gray-100" : ""
  return (
    <div className="border-1 border-gray-300 rounded">
      <div className={headerClass}
           onClick={() => setIsOpen((open) => !open)}>
        <span className={isOpen ? "text-blue-600 bg-gray-100" : ""}>
        {title}
        </span>
        <div>
          {isOpen
            ? <CaretUp size="1.25rem" />
            : <CaretDown size="1.25rem" />
          }
        </div>
      </div>

      {isOpen &&
      <div className="m-5">
        {children}
      </div>
      }
    </div>
  )
}