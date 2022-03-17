import React from 'react'
import { useRouteMatch } from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";
import SectionList from "app/views/laws/chapter/SectionList";
import { capitalize } from "app/lib/textUtils";


// TODO what to set header text to? (maybe I can handle that in Laws.js)
export default function ArticleView() {
  const match = useRouteMatch()
  const [ article, setArticle ] = React.useState({})
  const [ articleRelationships, setArticleRelationships ] = React.useState({})

  React.useEffect(() => {
    getLawsApi(match.params.chapterId)
      .then(response => {
        const article = response.documents.documents.items.find(a => {
          return a.lawId === match.params.chapterId && a.locationId === match.params.articleId
        })
        setArticle(article)
      })

    getLawsApi(match.params.chapterId, match.params.articleId)
      .then(response => setArticleRelationships(response))
  }, [ match ])

  console.log(article)
  console.log(articleRelationships)

  return (
    <section className="p-3">
      <header className="text-center">
        <h3 className="h3">{article.lawName}</h3>
        <h4 className="h4">{capitalize(article.docType)} {article.docLevelId}</h4>
        <h4 className="h5">{article.title}</h4>
      </header>
      <hr className="my-3" />

      <div className="my-3 bg-gray-300">
        Search Form
      </div>

      <div className="my-3">
        <a href={`/pdf/laws/${article.lawId}${article.locationId}?full=true`} target="_blank">
          View full article text as PDF
        </a>
      </div>

      <SectionList sections={article.documents?.items} articleLocationId={article.locationId} />

      {/*<div className="my-3">*/}
      {/*  {article?.documents?.items.map(section => (*/}
      {/*    <div key={section.locationId}>*/}
      {/*      {section.docType} {section.locationId}*/}
      {/*    </div>*/}
      {/*  ))}*/}
      {/*</div>*/}

      {/*<div className="mt-12 flex justify-center">*/}
      {/*TODO different px widths for different breakpoints*/}
      {/*  <pre className="w-[607px] text text--small">*/}
      {/*    {article.text}*/}
      {/*  </pre>*/}
      {/*</div>*/}

    </section>
  )
}
