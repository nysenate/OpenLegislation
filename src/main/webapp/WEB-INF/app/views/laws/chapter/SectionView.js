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
  NextSectionLink,
  PrevSectionLink
} from "app/views/laws/chapter/NavigationLinks";


export default function SectionView() {
  const match = useRouteMatch()
  const [ section, setSection ] = React.useState()

  React.useEffect(() => {
    getLawsApi(match.params.chapterId, match.params.sectionId)
      .then(response => {
        response.text = response.text.replaceAll("\\n", "\n")
        setSection(response)
      })
  }, [ match ])

  if (!section) {
    return null
  }

  return (
    <section className="p-3">
      <header className="text-center">
        <NavigationBar section={section} match={match} />
        <hr className="my-3" />
        <h3 className="h3 mt-6">{section.lawName}</h3>
        <h4 className="h4">{capitalize(section.docType)} {section.locationId}</h4>
        <h4 className="h5">{section.title}</h4>
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
            {section.text}
          </pre>
        </div>
      </div>
    </section>
  )
}

function NavigationBar({ section, match }) {
  return (
    <div className="grid grid-cols-3">
      <PrevSectionLink chapterId={section.lawId}
                       articleId={match.params.articleId}
                       sectionId={section.prevSibling?.locationId} />
      <BackToArticleLink article={section.parents.find(p => p.docType === "ARTICLE")} />
      <NextSectionLink chapterId={section.lawId}
                       articleId={match.params.articleId}
                       sectionId={section.nextSibling?.locationId} />
    </div>
  )
}
