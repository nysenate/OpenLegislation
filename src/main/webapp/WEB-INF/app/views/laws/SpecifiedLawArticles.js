import React from 'react'
import {
  Link,
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import SpecifiedLawArticleSections from "app/views/laws/SpecifiedLawArticleSections";
import * as queryString from "query-string";

export default function SpecifiedLawArticles({ response, term, limit, page, onPageChange }) {

  if (response.success === false) {
    return (
      <div>
        No results found
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="pt-3">
        <ResultList results={response} term={term} />
      </div>
    </div>
  )
}

function ResultList({ results, term }) {
  const documents = results.documents.documents.items
  return (
    <div>
      {documents.map((documents) =>
        <ResultItem result={documents} term={term} key={documents.locationId} />
      )}
    </div>
  )
}

function ResultItem({ result, term }) {
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search, { parseBooleans: true })

  return (
    <div>
      <div className="p-1 hover:bg-gray-200 flex" id={result.locationId} onClick={() => expandArticle()}>
        <div className=" py-6 w-full">

          <div className="grid grid-flow-col grid-rows-1 grid-cols-3 gap-4">

            <div className="text items-center mr-20">
              {result.docType}&nbsp;{result.docLevelId}
            </div>

            <div className="row-start-1 col-start-2 col-span-2">
              <div className=" items-center">
                <b>Sections&nbsp;(&sect;{result.fromSection}&nbsp;-&nbsp;&sect;{result.toSection})</b>
                <p>Location&nbsp;ID:{result.locationId}</p>
                <p>{result.title}</p>
              </div>
            </div>

          </div>


        </div>
      </div>
      <div className="">
        {(params.location === result.locationId || params.location === result.locationId + '#' + result.locationId
          || wasSearchedFor({ result, term })) &&
        (<div>
          <SpecifiedLawArticleSections response={result} term={term} />
        </div>)
        }
      </div>
    </div>

  )

  function expandArticle() {
    const params = queryString.parse(location.search)
    params.location = result.locationId
    history.push({ search: queryString.stringify(params) +'#'+ result.locationId })
  }

  function wasSearchedFor({ result, term }) {
    const resultFrom = result.fromSection
    const resultTo = result.toSection

    if (term >= resultFrom && term <= resultTo) {
      return true;
    }
    return false;

  }
}

