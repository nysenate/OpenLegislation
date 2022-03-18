import React from 'react'
import { Link } from "react-router-dom";


export default function SectionList({ sections = [], articleLocationId }) {
  return (
    <div>
      {sections.map(section => <SectionListRow section={section} key={section.locationId} />)}
    </div>
  )

  function SectionListRow({ section }) {
    return (
      <Link to={`/laws/${section.lawId}/${articleLocationId}/${section.locationId}`}>
        <div className="flex items-center text px-3 py-1 rounded hover:bg-gray-200">
          <div className="w-24 lg:w-32 flex-none">
            § {section.locationId}
          </div>
          <div className="text font-semibold">
            {section.title}
          </div>
        </div>
      </Link>
    )
  }
}