import React from "react"
import {
  CaretDown,
  CaretUp
} from "phosphor-react";


/**
 * Displays a truncated list with links to expand and collapse.
 * When collapsed, it shows the first 3 elements of the list.
 * @param list An array of components to render in a truncated list.
 * @returns {JSX.Element}
 * @constructor
 */
export default function TruncatedList({ list }) {
  const displaySize = 3
  const requiresTruncation = list.length > displaySize
  const [ isExpanded, setIsExpanded ] = React.useState(false)
  const [ displayedComponents, setDisplayedComponents ] = React.useState([])

  React.useEffect(() => {
    if (isExpanded) {
      setDisplayedComponents(list)
    } else {
      setDisplayedComponents(list.slice(0, displaySize))
    }
  }, [ isExpanded ])

  return (
    <div className="px-5">
      {displayedComponents.map((el, index) =>
        <div className="py-1" key={index}>
          {el}
        </div>
      )}
      {!isExpanded && requiresTruncation &&
        <div className="pt-1 flex items-center" onClick={() => setIsExpanded(true)}>
          <CaretDown size="1.25rem" weight="bold" className="text-blue-500 mr-1" />
          <a className="link">Show {list.length - displaySize} more</a>
        </div>
      }
      {isExpanded && requiresTruncation &&
        <div className="pt-1 flex items-center" onClick={() => setIsExpanded(false)}>
          <CaretUp size="1.25rem" weight="bold" className="text-blue-500 mr-1" />
          <a className="link">Show less</a>
        </div>
      }
    </div>
  )
}

