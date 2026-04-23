import React from 'react'
import { useRouteMatch } from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";
import { capitalize } from "app/lib/textUtils";
import {
  LawNavigationBar,
} from "app/views/laws/chapter/NavigationLinks";
import { FilePdf } from "phosphor-react";
import LawNodeChildrenList from "app/views/laws/chapter/LawNodeChildrenList";
import Select, { SelectOption } from "app/shared/Select";
import { useQueryParam } from "app/lib/urlUtils";
import * as queryString from "query-string";


export default function LawNodeView({ setHeaderText }) {
  const match = useRouteMatch()

  const [ nodeTree, setNodeTree ] = React.useState()
  const [ node, setNode ] = React.useState()
  const [ date, setDate ] = useQueryParam("date")
  const [ dateOptions, setDateOptions ] = React.useState()
  const findNext = queryString.parse(location.search, { parseBooleans: true })["findNext"]

  React.useEffect(() => {
    getLawsApi(match.params.chapterId, null, { fromLocation: match.params.locationId, date })
      .then(response => setNodeTree(response.documents))

    getLawsApi(match.params.chapterId, match.params.locationId, date ? { date } : {})
      .then(response => setNode(response))
  }, [ match, date ])

  React.useEffect(() => {
    setHeaderText(node?.lawName || "")
    if (!node) return
    const options = [...node.publishedDates].reverse().map((t) => new SelectOption(t, t))
    setDateOptions(options)
    if (date) {
      let closest
      if (findNext) {
        closest = options.findLast(o => o.value >= date)
      }
      else {
        closest = options.find(o => o.value <= date)
      }
      if (closest && closest.value !== date) setDate(closest.value)
    }
    else {
      setDate(options[0]?.value)
    }
  }, [ node ])

  if (!node || !nodeTree) {
    return null
  }

  // TODO: LawNavigationBar should keep dates in mind
  return (
    <section className="p-3">
      <header className="text-center">
        <LawNavigationBar node={node} docType={node.docType} />
        <hr className="my-3" />
        <h3 className="h3">{node.lawName}</h3>
        <h4 className="h4">{capitalize(node.docType)} {node.docLevelId}</h4>
        <h4 className="h5">{node.title}</h4>
      </header>

      <Select label="View Historical Revision as of:"
              name=" Historical Revision"
              value={date}
              options={dateOptions}
              onChange={(e) => setDate(e.target.value)} />

      <div className="my-5 flex items-center">
        <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
        <a href={`/pdf/laws/${node.lawId}${node.locationId}?full=true&date=${date}`} target="_blank" className="link">
          View full {node.docType.toLocaleLowerCase()} as PDF
        </a>
      </div>

      <LawNodeChildrenList nodes={nodeTree.documents?.items} date={date} />
    </section>
  )
}
