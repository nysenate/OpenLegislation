import {
  ArrowUUpLeft,
  CaretLeft,
  CaretRight
} from "phosphor-react";
import { Link } from "react-router-dom";
import React from "react";


function NextSectionLink({ chapterId, articleId, sectionId }) {
  return sectionId
    ? <NavigationLink label="Next Section" type="next" to={`/laws/${chapterId}/${articleId}/${sectionId}`} />
    : <div></div>
}

function PrevSectionLink({ chapterId, articleId, sectionId }) {
  return sectionId
    ? <NavigationLink label="Previous Section" type="prev" to={`/laws/${chapterId}/${articleId}/${sectionId}`} />
    : <div></div>
}

function BackToArticleLink({ article }) {
  return article
    ? <NavigationLink label={`Back to Article ${article.locationId}`}
                      type="up"
                      to={`/laws/${article.lawId}/${article.locationId}`} />
    : <div></div>
}

function NavigationLink({ label, type, to }) {
  let icon
  let containerClass = "flex"
  switch (type) {
    case "up":
      containerClass += " justify-center"
      icon = <ArrowUUpLeft size="1.5rem" weight="bold" className="mr-1" />
      break
    case "next":
      containerClass += " justify-end"
      icon = <CaretRight size="1.5rem" weight="bold" className="mr-1" />
      break
    case "prev":
      containerClass += " justify-start"
      icon = <CaretLeft size="1.5rem" weight="bold" className="mr-1" />
      break
  }

  return (
    <div className={containerClass}>
      <Link to={to} className="border-0">
        <div className="flex items-center p-3 hover:bg-gray-200 rounded">
          {type === "next"
            ? <>{label} {icon}</>
            : <>{icon} {label}</>
          }
        </div>
      </Link>
    </div>
  )
}

export { NextSectionLink, PrevSectionLink, BackToArticleLink }
