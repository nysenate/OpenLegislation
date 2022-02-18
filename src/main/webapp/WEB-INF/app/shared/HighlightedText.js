import React from "react";

/**
 * Displays text, some of which is highlighted, with a gray background.
 */
export default function HighlightedText({highlights}) {
  if (!highlights)
    return ""
  const textArray = highlights.text
  return (
    <ol>{textArray.map((textSegment) =>
      <li key = {textSegment}>
          <pre style = {{background: "#f5f5f5"}}>
            <span className = "highlight" dangerouslySetInnerHTML =
              {{__html: textSegment.replaceAll("\n", "<br/>")}} /><br/>
          </pre>
      </li>
    )}
    </ol>
  )
}