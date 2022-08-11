import React from 'react'
import Accordion from "app/shared/Accordion";

export default function QuickSearchTips() {
  return (
    <Accordion title="Quick Query Search Tips">
      <div className="m-5">
        <p className="text text--small">
          Each bill and resolution has a print number and session year. If you are looking for a specific
          piece of legislation, you can simply enter it's print number in the search box, e.g. <code>S1234-2013</code>.
        </p>
        <p className="text text--small mt-2">
          If you would like to search for legislation where a certain term or phrase appears, you can enter
          the term in the search box, e.g. <code>public schools</code>. If you want to match a specific phrase
          you will need to enter it in quotes, e.g. <code>"Start UP NY"</code>. For more advanced queries see below.
        </p>
      </div>
    </Accordion>
  )
}