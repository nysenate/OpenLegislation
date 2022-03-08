import React from "react";

/**
 * Displays text, some of which is highlighted, with a gray background.
 */
export default function HighlightedText({ highlights }) {
  if (!highlights)
    return ""

  const textArray = highlights.text || []
  return (
    <div className="bg-gray-100 rounded p-3 overflow-x-auto">
      {textArray.map((textSegment, i) =>
        <div key={i}>
          <pre>
            <span className="text text--small highlight"
                  dangerouslySetInnerHTML={{ __html: textSegment }} />
          </pre>
        </div>
      )}
    </div>
  )
}
