import React from 'react'
import { useRouteMatch } from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";
import SectionList from "app/views/laws/chapter/SectionList";
import { capitalize } from "app/lib/textUtils";
import {
  BackToChapterLink,
  NextArticleLink,
  PrevArticleLink
} from "app/views/laws/chapter/NavigationLinks";
import { FilePdf } from "phosphor-react";


export default function ArticleView() {
  const match = useRouteMatch()
  const [ articleTree, setArticleTree ] = React.useState()
  const [ article, setArticle ] = React.useState()

  React.useEffect(() => {
    getLawsApi(match.params.chapterId)
      .then(response => {
        const articleTree = response.documents.documents.items.find(a => {
          return a.lawId === match.params.chapterId && a.locationId === match.params.articleId
        })
        setArticleTree(articleTree)
      })

    getLawsApi(match.params.chapterId, match.params.articleId)
      .then(response => setArticle(response))
  }, [ match ])

  if (!article || !articleTree) {
    return null
  }

  return (
    <section className="p-3">
      <header className="text-center">
        <NavigationBar article={article} />
        <hr className="my-3" />
        <h3 className="h3">{article.lawName}</h3>
        <h4 className="h4">{capitalize(article.docType)} {article.docLevelId}</h4>
        <h4 className="h5">{article.title}</h4>
      </header>

      <div className="my-5 flex items-center">
        <FilePdf className="inline mr-1 text-blue-500" size="1.5rem" />
        <a href={`/pdf/laws/${article.lawId}${article.locationId}?full=true`} target="_blank">
          View full article text as PDF
        </a>
      </div>

      <SectionList sections={articleTree.documents?.items} articleLocationId={article.locationId} />
    </section>
  )
}

function NavigationBar({ article }) {
  return (
    <div className="grid grid-cols-3">
      <PrevArticleLink prevArticle={article.prevSibling} />
      <BackToChapterLink chapter={article.parents.find(p => p.docType === "CHAPTER")} />
      <NextArticleLink nextArticle={article.nextSibling} />
    </div>
  )
}

