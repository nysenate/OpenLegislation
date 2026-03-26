import React from 'react'
import {
  Link,
  useRouteMatch
} from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";
import { capitalize } from "app/lib/textUtils";
import {
  FilePdf
} from "phosphor-react";
import {
  LawNavigationBar,
} from "app/views/laws/chapter/NavigationLinks";
import Select, { SelectOption } from "app/shared/Select";
import { useQueryParam } from "app/lib/urlUtils";


/**
 * Displays the text for a leaf node of a law document tree.
 */
export default function LawLeafNodeView({ setHeaderText }) {
  const match = useRouteMatch()

  const [ leafNode, setLeafNode ] = React.useState()
  const [ date, setDate ] = useQueryParam("date")
  const [ dateOptions, setDateOptions ] = React.useState()

  React.useEffect(() => {
    getLawsApi(match.params.chapterId, match.params.locationId, date ? { date } : {})
      .then(response => {
        response.text = response.text.replaceAll("\\n", "\n")
        setLeafNode(response)
      })
  }, [ match, date ])

  React.useEffect(() => {
    setHeaderText(leafNode?.lawName || "")
    if (!leafNode) return
    const options = [...leafNode.publishedDates].reverse().map((t) => new SelectOption(t, t))
    setDateOptions(options)
    if (date) {
      const closest = options.find(o => o.value <= date)
      if (closest && closest.value !== date) setDate(closest.value)
    }
    else {
      setDate(options[0]?.value)
    }
  }, [leafNode])

  if (!leafNode) {
    return null
  }

  // TODO: LawNavigationBar should keep dates in mind
  return (
    <section className="p-3">
      <header className="text-center">
        <LawNavigationBar node={leafNode} docType={leafNode.docType} />
        <hr className="my-3" />
        <h3 className="h3">{leafNode.lawName}</h3>
        <h4 className="h4">{capitalize(leafNode.docType)} {leafNode.locationId}</h4>
        <h4 className="h5">{leafNode.title}</h4>
      </header>

      <Select label="View Historical Revision as of:"
              name="Historical Revision"
              value={date}
              options={dateOptions}
              onChange={(e) => setDate(e.target.value)} />

      <div className="my-5 overflow-x-auto md:flex md:justify-center">
        <div>
          <div className="flex items-center">
            <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
            <Link to={`/pdf/laws/${match.params.chapterId}${match.params.locationId}?full=true`} target="_blank" className="link">
              View as PDF
            </Link>
          </div>
          <pre className="whitespace-normal md:whitespace-pre-wrap my-5 text text--small">
            {leafNode.text}
          </pre>
        </div>
      </div>
    </section>
  )
}
