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
  BackToArticleLink,
  NavigationLink,
  NextSectionLink,
  PrevSectionLink,
  Spacer
} from "app/views/laws/chapter/NavigationLinks";


/**
 * Displays the text for a leaf node of a law document tree.
 */
export default function LawLeafNodeView() {
  const match = useRouteMatch()
  const [ leafNode, setLeafNode ] = React.useState()

  React.useEffect(() => {
    getLawsApi(match.params.chapterId, match.params.locationId)
      .then(response => {
        response.text = response.text.replaceAll("\\n", "\n")
        setLeafNode(response)
      })
  }, [ match ])

  if (!leafNode) {
    return null
  }

  return (
    <section className="p-3">
      <header className="text-center">
        <LeafNavigationBar node={leafNode} />
        <hr className="my-3" />
        <h3 className="h3">{leafNode.lawName}</h3>
        <h4 className="h4">{capitalize(leafNode.docType)} {leafNode.locationId}</h4>
        <h4 className="h5">{leafNode.title}</h4>
      </header>

      <div className="my-5 overflow-x-auto md:flex md:justify-center">
        <div>
          <div className="flex items-center">
            <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
            <Link to={`/pdf/laws/${match.params.chapterId}${match.params.sectionId}?full=true`} target="_blank">
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

function LeafNavigationBar({ node }) {
  const [parent] = node.parents.slice(-1)
  const parentTo = parent.docType === "CHAPTER"
    ? `/laws/${node.lawId}`
    : `/laws/${node.lawId}/node/${parent.locationId}`
  return (
    <div className="grid grid-cols-3">
      {node.prevSibling
        ? <NavigationLink type="prev"
                          label={`Previous ${capitalize(node.prevSibling.docType)}`}
                          to={`/laws/${node.lawId}/leaf/${node.prevSibling.locationId}`} />
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
                          to={`/laws/${node.lawId}/leaf/${node.nextSibling.locationId}`} />
        : <Spacer />
      }
    </div>
  )
}
