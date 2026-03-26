import React from 'react'
import {
  Link,
} from "react-router-dom";
import { capitalize } from "app/lib/textUtils";

export default function LawNodeChildrenList({ nodes, date }) {
  return (
    <div>
      {nodes.map(node => <NodeRow node={node} date={date} key={node.locationId} />)}
    </div>
  )
}

function NodeRow({ node, date }) {
  const to = node.docType === "SECTION"
    ? `/laws/${node.lawId}/leaf/${node.locationId}?date=${date}`
    : `/laws/${node.lawId}/node/${node.locationId}?date=${date}`
  return (
    <Link to={to} className="link border-0">
      <div className="flex items-center text px-3 py-1 rounded hover:bg-gray-200">
        <div className="w-24 lg:w-32 flex-none">
          {capitalize(node.docType)} {node.docLevelId}
        </div>
        <div>
          <div className="text font-semibold">
            {node.title}
          </div>
          {node.docType !== "SECTION" &&
            <div className="text text--small">
              Sections (§{node.fromSection} - §{node.toSection})
            </div>
          }
        </div>
      </div>
    </Link>
  )
}
