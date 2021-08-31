import React from 'react'
import {Link, useHistory, useLocation, useRouteMatch} from "react-router-dom";
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
    const params = queryString.parse(location.search, {parseBooleans: true})

    return (
        <div className="p-3 hover:bg-gray-200 flex flex-wrap" onClick={() => expandArticle()}>
            <div className=" py-4 w-full md:w-1/3">
                <div className="">{/*<div className="flex items-center">*/}

                    <div className="text mr-20">
                        <p>{result.docType}&nbsp;{result.docLevelId}</p>
                    </div>

                    <div className="text text--small">
                        <b>Sections&nbsp;(&sect;{result.fromSection}&nbsp;-&nbsp;&sect;{result.toSection})&nbsp;-&nbsp;Location&nbsp;ID:&nbsp;{result.locationId}</b>
                        <p>{result.title}</p>
                    </div>

                    <div className="flex-flow:column-wrap flex-end">
                        {params.location === result.locationId &&
                        (<div>
                            <SpecifiedLawArticleSections response={result}/>
                        </div>)
                        }
                    </div>

                </div>
            </div>
        </div>
    )

    function expandArticle() {
        const params = queryString.parse(location.search)
        params.location = result.locationId
        history.push({search: queryString.stringify(params)})
    }
}

