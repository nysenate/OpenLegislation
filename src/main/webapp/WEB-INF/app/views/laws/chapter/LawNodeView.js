import React from 'react'
import { useRouteMatch } from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";
import { capitalize } from "app/lib/textUtils";
import {
  NavigationLink,
  Spacer
} from "app/views/laws/chapter/NavigationLinks";
import { FilePdf } from "phosphor-react";
import LawNodeChildrenList from "app/views/laws/chapter/LawNodeChildrenList";


export default function LawNodeView({ setHeaderText }) {
  const match = useRouteMatch()
  const [ nodeTree, setNodeTree ] = React.useState()
  const [ node, setNode ] = React.useState()

  React.useEffect(() => {
    getLawsApi(match.params.chapterId, null, { fromLocation: match.params.locationId })
      .then(response => setNodeTree(response.documents))

    getLawsApi(match.params.chapterId, match.params.locationId)
      .then(response => setNode(response))
  }, [ match ])

  React.useEffect(() => {
    setHeaderText(node?.lawName || "")
  }, [ node ])

  if (!node || !nodeTree) {
    return null
  }

  return (
    <section className="p-3">
      <header className="text-center">
        <NodeNavigationBar node={node} />
        <hr className="my-3" />
        <h3 className="h3">{node.lawName}</h3>
        <h4 className="h4">{capitalize(node.docType)} {node.docLevelId}</h4>
        <h4 className="h5">{node.title}</h4>
      </header>

      <div className="my-5 flex items-center">
        <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
        <a href={`/pdf/laws/${node.lawId}${node.locationId}?full=true`} target="_blank">
          View full {node.docType.toLocaleLowerCase()} as PDF
        </a>
      </div>

      <LawNodeChildrenList nodes={nodeTree.documents?.items} />
    </section>
  )
}

function NodeNavigationBar({ node }) {
  const [ parent ] = node.parents.slice(-1)
  const parentTo = parent.docType === "CHAPTER"
    ? `/laws/${node.lawId}`
    : `/laws/${node.lawId}/node/${parent.locationId}`
  return (
    <div className="grid grid-cols-3">
      {node.prevSibling
        ? <NavigationLink type="prev"
                          label={`Previous ${capitalize(node.prevSibling.docType)}`}
                          to={`/laws/${node.lawId}/node/${node.prevSibling.locationId}`} />
        : <Spacer />
      }
      {parent
        ? <NavigationLink type="up"
                          label={`Back to ${capitalize(parent.docType)}`}
                          to={parentTo} />
        : <Spacer />
      }
      {node.nextSibling
        ? <NavigationLink type="next"
                          label={`Next ${capitalize(node.nextSibling.docType)}`}
                          to={`/laws/${node.lawId}/node/${node.nextSibling.locationId}`} />
        : <Spacer />
      }
    </div>
  )
}

