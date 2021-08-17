import React from 'react'
import Pagination from "app/shared/Pagination";
import {Link, useHistory, useLocation, useRouteMatch} from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";
import SpecifiedLawArticleSections from "app/views/laws/SpecifiedLawArticleSections";
import * as queryString from "query-string";

export default function SpecifiedLawArticles({response, limit, page, onPageChange}) {

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
                {/*<Pagination*/}
                {/*    limit={limit}*/}
                {/*    currentPage={page}*/}
                {/*    onPageChange={onPageChange}*/}
                {/*    total={response.documents.documents.items.length}*/}
                {/*/>*/}
                <ResultList results={response}/>
                {/*<Pagination*/}
                {/*    limit={limit}*/}
                {/*    currentPage={page}*/}
                {/*    onPageChange={onPageChange}*/}
                {/*    total={response.documents.documents.items.length}*/}
                {/*/>*/}
            </div>
        </div>
    )
}

function ResultList({results}) {
    const documents = results.documents.documents.items
    return (
        <div>
            {documents.map((documents) =>
                <ResultItem result={documents} key={documents.locationId}/>
            )}
        </div>
    )
}

function ResultItem({result}) {
    // console.log(result)
    const location = useLocation()
    const history = useHistory()
    const params = queryString.parse(location.search, { parseBooleans: true })

    return (
        <div className="p-3 hover:bg-gray-200 flex flex-wrap" onClick={() => expandArticle()}>
            <div className="flex items-center w-full md:w-1/3">
                <div>

                    <div className="text">
                        {result.docType} {result.docLevelId}
                    </div>

                    <div className="text text--small">
                        <b>Sections (&sect;{result.fromSection} - &sect;{result.toSection}) - Location ID: {result.locationId}</b>
                        <p> {result.title} </p>
                    </div>

                    {params.location === result.locationId &&
                    (<div>
                        <SpecifiedLawArticleSections response={result}/>
                    </div>)
                    }
                </div>
            </div>
        </div>
    )

    function expandArticle() {
        const params = queryString.parse(location.search)
        params.location = result.locationId
        history.push({ search: queryString.stringify(params) })
    }
}

