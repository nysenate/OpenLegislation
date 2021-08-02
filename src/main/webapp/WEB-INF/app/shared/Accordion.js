import React from 'react'
import {
  CaretDown,
  CaretUp,
} from "phosphor-react";


export default function Accordion({ title, children }) {
  const [ isOpen, setIsOpen ] = React.useState(false);

  return (
    <div className="border-1 border-gray-300 rounded">
      <div className="flex justify-between items-center p-3 rounded cursor-pointer hover:bg-gray-100"
           onClick={() => setIsOpen((open) => !open)}>
        {title}
        <div>
          {isOpen
            ? <CaretDown size="1.25rem" />
            : <CaretUp size="1.25rem" />
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