import React from "react";

/**
 * Displays text, some of which is highlighted, with a gray background.
 * @param highlights An array of highlighted text entries.
 */
export default function HighlightedText({ highlights }) {
  if (!highlights)
    return null

  return (
    <div className="bg-gray-100 rounded p-3 overflow-x-auto">
      {highlights.map((textSegment, i) =>
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
