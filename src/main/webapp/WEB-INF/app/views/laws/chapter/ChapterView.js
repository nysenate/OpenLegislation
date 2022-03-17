import React from 'react';
import getLawsApi from "app/apis/getLawsApi";
import {
  useLocation,
  useHistory,
  useRouteMatch
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import ArticleList from "app/views/laws/chapter/ArticleList";
import ChapterSectionFilter from "app/views/laws/chapter/ChapterSectionFilter";


export default function ChapterView({ setHeaderText }) {
  const match = useRouteMatch()
  const [ chapter, setChapter ] = React.useState({})
  const [ isLoading, setIsLoading ] = React.useState(true)


  // const [ term, setTerm ] = React.useState("*")
  // const location = useLocation()
  // const history = useHistory()
  // const params = queryString.parse(location.search)
  // const limit = 6

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
        <h3 className="h3">{chapter.info.name}</h3>
        <div className="text mt-1">{chapter.info.lawType} | Chapter {chapter.info.chapter}</div>
      </header>
      <hr className="my-3" />

      <div className="my-3 bg-gray-300">
        Search Form
      </div>

      <div className="my-3">
        {/*TODO view full text as: text | pdf ??? (Is it possible to do this?*/}
        <a href={`/pdf/laws/${chapter.info.lawId}?full=true`} target="_blank">View full chapter text as PDF</a>
      </div>

      <ArticleList articles={chapter.documents.documents.items} />

    </section>
  )

  // const onPageChange = pageInfo => {
  //   params.page = pageInfo.selectedPage
  //   history.push({ search: queryString.stringify(params) })
  // }
  //
  // return (
  //   <div className="p-3">
  //     <ChapterSectionFilter setTerm={setTerm} />
  //     {loading
  //       ? <LoadingIndicator />
  //       : <ArticleList articles={response.documents.documents.items}
  //                      term={term}
  //                      limit={limit}
  //                      page={params.page}
  //                      onPageChange={onPageChange} />
  //     }
  //   </div>
  // )
}
