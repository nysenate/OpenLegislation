import React from 'react'
import Pagination from "app/shared/Pagination";

export default function CalendarUpdatesSearchResults({ response, limit, page, onPageChange, detail }) {

    // console.log(response)

    if (response.total === 0) {
        return (
            <div>
                No results found
            </div>
        )
    }

    return (
        <div className="mt-8">
            <div className="pt-3">

                <Pagination
                    limit={limit}
                    currentPage={page}
                    onPageChange={onPageChange}
                    total={response.total}
                />
                <ResultList results={response.result.items} detail={detail} />
                <Pagination
                    limit={limit}
                    currentPage={page}
                    onPageChange={onPageChange}
                    total={response.total}
                />

            </div>
        </div>
    )
}

function ResultList({results, detail}) {
    return (
        <div>
            {results.map((r) =>
                <ResultItem result={r} detail={detail} key={r.processedDateTime}/>
            )}
        </div>
    )
}

function ResultItem({result, detail}) {
    console.log(result)
    console.log(detail)
    const fields = result.fields
    return (
        <div>
            <div className="p-3 hover:bg-gray-200 flex flex-wrap">
                <div className="py-3 w-full">

                    <div className="grid grid-flow-col grid-rows-4 grid-cols-1 gap-4">

                        <div className="row-start-1 flex items-center text mr-5">
                            { detail && <b>{result.action} - {result.scope} {result.id.calendarNumber} ({result.id.year})</b> }
                            { !detail && <b>{result.id.calendarNumber} ({result.id.year}</b> }
                        </div>

                        <div className="row-start-2">
                            <div className="flex items-center text text--small">
                                <p><b>Published Date - {result.sourceDateTime} </b></p>
                            </div>
                        </div>

                        <div className="row-start-3">
                            <div className="flex items-center text text--small">
                                <p><b>Processed Date - {result.processedDateTime} </b></p>
                            </div>
                        </div>

                        <div className="row-start-4">
                            <div className="flex items-center text text--small">
                                <p><b>Source - {result.sourceId} </b></p>
                            </div>
                        </div>

                    </div>

                    {detail &&
                    <table className="mt-5 gap-4">
                        <thead>
                        <tr>
                            <th>Field Name</th>
                            <th>Data</th>
                        </tr>
                        </thead>

                        <tbody>
                        <tr>
                            <td>Created Date Time</td>
                            <td>{fields["Created Date Time"]}</td>
                        </tr>
                        <tr>
                            <td>Calendar Date</td>
                            <td>{fields["Calendar Date"]}</td>
                        </tr>
                        <tr>
                            <td>Published Date Time</td>
                            <td>{fields["Published Date Time"]}</td>
                        </tr>
                        <tr>
                            <td>Id</td>
                            <td>{fields["Id"]}</td>
                        </tr>
                        <tr>
                            <td>Sup Version</td>
                            <td>{fields["Sup Version"]}</td>
                        </tr>
                        <tr>
                            <td>Release Date Time</td>
                            <td>{fields["Release Date Time"]}</td>
                        </tr>
                        </tbody>
                        <tfoot>
                        </tfoot>

                    </table>
                    }

                </div>
            </div>
        </div>
    )
}