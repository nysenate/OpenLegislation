import React from 'react';
import getLawsApi from "app/apis/getLawsApi";
import {
  useRouteMatch
} from "react-router-dom";
import LoadingIndicator from "app/shared/LoadingIndicator";
import LawNodeChildrenList from "app/views/laws/chapter/LawNodeChildrenList";
import {
  LawNavigationBar,
} from "app/views/laws/chapter/NavigationLinks";
import { FilePdf } from "phosphor-react";


export default function ChapterView({ setHeaderText }) {
  const match = useRouteMatch()
  const [ chapter, setChapter ] = React.useState()
  const [ isLoading, setIsLoading ] = React.useState(true)

  React.useEffect(() => {
    setIsLoading(true)
    getLawsApi(match.params.chapterId, null)
      .then(response => setChapter(response))
      .catch(error => console.warn(`${error}`))
      .finally(() => setIsLoading(false))
  }, [ match ])

  React.useEffect(() => {
    setHeaderText(chapter?.info?.name || "")
  }, [ chapter ])

  if (isLoading) {
    return <LoadingIndicator />
  }

  return (
    <section className="p-3">
      <header className="text-center">
        <LawNavigationBar node={chapter} docType={chapter.documents.docType} />
        <hr className="my-3" />
        <h3 className="h3">{chapter.info.name}</h3>
        <div className="text mt-1">{chapter.info.lawType} | Chapter {chapter.info.chapter}</div>
      </header>

      <div className="my-5 flex items-center">
        <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
        <a href={`/pdf/laws/${chapter.info.lawId}?full=true`} target="_blank" className="link">View full chapter text as PDF</a>
      </div>

      <LawNodeChildrenList nodes={chapter.documents.documents.items} />
    </section>
  )
}
