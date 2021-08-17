import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";

export default function LawVolumeSearchResults({response, limit, page, onPageChange}) {

    if (response.items.length === 0) {
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
                {/*    total={response.size}*/}
                {/*/>*/}
                <ResultList results={response.items}/>
                {/*<Pagination*/}
                {/*    limit={limit}*/}
                {/*    currentPage={page}*/}
                {/*    onPageChange={onPageChange}*/}
                {/*    total={response.size}*/}
                {/*/>*/}
            </div>
        </div>
    )
}

function ResultList({results}) {
    return (
        <div>
            {results.map((r) =>
                <Link to={`/laws/${r.lawId}`}
                      key={r.lawId}>
                <ResultItem result={r} key={r.lawId}/>
                </Link>
            )}
        </div>
    )
}

function ResultItem({result}) {
    const volume = result
    return (
        <div className="p-3 hover:bg-gray-200 flex flex-wrap">
            <div className="flex items-center w-full md:w-1/3">
                <div>
                    <div className="text">
                        {volume.lawId}
                    </div>
                    <div className="text text--small">
                        <b>{volume.name}</b>
                        <p> {volume.lawType} | {volume.chapter} </p>
                    </div>
                </div>
            </div>
        </div>
    )
}