import React from 'react'
import {
  Link,
} from "react-router-dom";
import { capitalize } from "app/lib/textUtils";

export default function ArticleList({ articles }) {
  return (
    <div>
      {articles.map(article => <ArticleListRow article={article} key={article.locationId} />)}
    </div>
  )
}

function ArticleListRow({ article }) {
  return (
    <Link to={`/laws/${article.lawId}/${article.locationId}`}>
      <div className="flex items-center text px-3 py-1 rounded hover:bg-gray-200">
        <div className="w-24 lg:w-32 flex-none">
          {capitalize(article.docType)} {article.docLevelId}
        </div>
        <div>
          <div className="text font-semibold">
            {article.title}
          </div>
          <div className="text text--small">
            Sections (§{article.fromSection} - §{article.toSection})
          </div>
        </div>
      </div>
    </Link>
  )
}
