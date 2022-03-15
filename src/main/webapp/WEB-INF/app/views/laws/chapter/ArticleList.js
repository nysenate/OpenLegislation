import React from 'react'
import {
  useHistory,
  useLocation,
} from "react-router-dom";
import SectionAccordion from "app/views/laws/chapter/SectionAccordion";
import * as queryString from "query-string";
import Accordion from "app/shared/Accordion";

export default function ArticleList({ articles }) {
  return (
    <div className="mt-8">
      <div className="pt-3">
        {articles.map((article) =>
          <ArticleAccordion article={article} key={article.lawId + article.locationId} />
        )}
      </div>
    </div>
  )
}

function ArticleAccordion({ article }) {
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search, { parseBooleans: true })

  return (
    <div>
      <Accordion title={<ArticleAccordionTitle article={article} />}
                 children={<ArticleAccordionContent article={article} />}
                 type="laws" />
    </div>
  )
}

function ArticleAccordionTitle({article}) {
  return (
    <div className="flex items-center text">
      <div className="w-24 lg:w-32 flex-none">
        {article.docType} {article.docLevelId}
      </div>
      <div className="">
        <div>
          <span className="text font-semibold">Sections (§{article.fromSection} - §{article.toSection})</span> -
          LocationId: {article.locationId}
        </div>
        <div className="text text--small">
          {article.title}
        </div>
      </div>
    </div>
  )
}

function ArticleAccordionContent({article}) {
  return (
    <>
      <div className="ml-5 lg:ml-16">
        {article.documents.items.map(section => <SectionAccordion section={section} />)}
      </div>
      <hr className="mb-3"/>
    </>
  )
}

