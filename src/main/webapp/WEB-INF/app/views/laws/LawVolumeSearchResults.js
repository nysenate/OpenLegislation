import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";

export default function LawVolumeSearchResults({response, filter}) {

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

                <ResultList results={response.items} filter={filter}/>
            </div>
        </div>
    )
}

function ResultList({results, filter}) {
    return (
        <div>
            {results.map((r) =>
                <Link to={`/laws/${r.lawId}`}
                      key={r.lawId}>
                <ResultItem result={r} filter={filter} key={r.lawId}/>
                </Link>
            )}
        </div>
    )
}

function ResultItem({result , filter}) {
    const volume = result
    const doFilterResult = doFilter(result, filter)
    // const doFilterResult = true
    return (
        <div>
            { doFilterResult &&
            <div className="p-3 hover:bg-gray-200 flex flex-wrap">
                <div className="py-3 w-full md:w-1/3">
                    <div className="flex items-center">
                        <div className="text mr-5">
                            {volume.lawId}
                        </div>
                        <div className="text text--small">
                            <b>{volume.name}</b>
                            <p> {volume.lawType} | Chapter {volume.chapter} </p>
                        </div>
                    </div>
                </div>
            </div> }
        </div>
    )
}

function doFilter(result,filter) {
    if ( filter === undefined || isEmpty(filter) ) {
        return true;
    }
    return result.name.toLowerCase().includes(filter.toLowerCase());
}

function isEmpty(str) {
    return (!str || str.length === 0 );
}