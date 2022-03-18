import React from 'react';
import getLawsApi from "app/apis/getLawsApi";
import {
  useRouteMatch
} from "react-router-dom";
import LoadingIndicator from "app/shared/LoadingIndicator";
import ArticleList from "app/views/laws/chapter/ArticleList";
import {
  BackToLawSearchLink,
  Spacer
} from "app/views/laws/chapter/NavigationLinks";
import { FilePdf } from "phosphor-react";


export default function ChapterView({ setHeaderText }) {
  const match = useRouteMatch()
  const [ chapter, setChapter ] = React.useState({})
  const [ isLoading, setIsLoading ] = React.useState(true)

  React.useEffect(() => {
    setHeaderText(match.params.chapterId)
    setIsLoading(true)
    getLawsApi(match.params.chapterId, null)
      .then(response => setChapter(response))
      .catch(error => console.warn(`${error}`))
      .finally(() => setIsLoading(false))
  }, [ match ])

  if (isLoading) {
    return <LoadingIndicator />
  }

  return (
    <section className="p-3">
      <header className="text-center">
        <NavigationLinks />
        <hr className="my-3" />
        <h3 className="h3">{chapter.info.name}</h3>
        <div className="text mt-1">{chapter.info.lawType} | Chapter {chapter.info.chapter}</div>
      </header>

      <div className="my-5 bg-gray-300">
        Search Form stub
      </div>

      <div className="my-3 flex items-center">
        <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
        <a href={`/pdf/laws/${chapter.info.lawId}?full=true`} target="_blank">View full chapter text as PDF</a>
      </div>

      <ArticleList articles={chapter.documents.documents.items} />
    </section>
  )
}

function NavigationLinks() {
  return (
    <div className="grid grid-cols-3">
      <Spacer />
      <BackToLawSearchLink />
      <Spacer />
    </div>
  )
}
