import React from 'react'
import {
  Link,
  useHistory,
  useLocation,
} from "react-router-dom";
import SectionAccordion from "app/views/laws/chapter/SectionAccordion";
import * as queryString from "query-string";
import Accordion from "app/shared/Accordion";
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

//   return (
//     <div className="mt-8">
//       <div className="pt-3">
//         {articles.map((article) =>
//           <ArticleAccordion article={article} key={article.lawId + article.locationId} />
//         )}
//       </div>
//     </div>
//   )
// }
//
// function ArticleAccordion({ article }) {
//   const location = useLocation()
//   const history = useHistory()
//   const params = queryString.parse(location.search, { parseBooleans: true })
//
//   return (
//     <div>
//       <Accordion title={<ArticleAccordionTitle article={article} />}
//                  children={<ArticleAccordionContent article={article} />}
//                  type="laws" />
//     </div>
//   )
// }
//
// function ArticleAccordionContent({article}) {
//   return (
//     <>
//       <div className="ml-5 lg:ml-16">
//         {article.documents.items.map(section => <SectionAccordion section={section} />)}
//       </div>
//       <hr className="mb-3"/>
//     </>
//   )
// }
//
