import React from 'react'
import {
    useHistory,
    useLocation
} from "react-router-dom";
import * as queryString from "query-string";


export default function LawSearchForm({updateValues, aMonthAgo, todaysDate}) {

    React.useEffect(() => {
        let LawUpdateFrom = document.getElementById("LawUpdateFrom")
        LawUpdateFrom.value = aMonthAgo
        let lawUpdateTo = document.getElementById("lawUpdateTo")
        lawUpdateTo.value = todaysDate
    }, [])

    // Updates the term query param when the form is submitted.
    const onSubmitLawUpdatesSearch = (e) => {
        e.preventDefault()

        const LawUpdateFrom = document.getElementById("LawUpdateFrom")
        console.log(LawUpdateFrom.value)

        const lawUpdateTo = document.getElementById("lawUpdateTo")
        console.log(lawUpdateTo.value)

        const withSelect = document.getElementById("with")
        console.log(withSelect.value)

        const sortSelect = document.getElementById("sort")
        console.log(sortSelect.value)

        updateValues(LawUpdateFrom.value, lawUpdateTo.value, withSelect.value, sortSelect.value);
    }

    return (
        <div>

            <form onSubmit={onSubmitLawUpdatesSearch}>
                <div className="flex flex-wrap">
                    <div className="flex-grow mr-8">

                        <h1 className="p-5 font-bold">Show law updates during the following date range</h1>

                        <label htmlFor="LawUpdateFrom" className="label label--left uppercase font-bold p-2">From:</label>
                        <input className="pl-4" type="date" id="LawUpdateFrom" name="LawUpdateFrom" tabIndex="2"/>

                        <label htmlFor="lawUpdateTo" className="label label--left uppercase font-bold p-2">To:</label>
                        <input className="pl-4" type="date" id="lawUpdateTo" name="lawUpdateTo" tabIndex="3"/>

                        <label htmlFor="with" className="label label--left uppercase font-bold p-2">With:</label>
                        <select className="pl-4" name="with" id="with" tabIndex="4">
                            <option value="published">Published Date</option>
                            <option value="processed">Processed Date</option>
                        </select>

                        <label htmlFor="sort" className="label label--left uppercase font-bold p-2">Sort:</label>
                        <select className="pl-4" name="sort" id="sort" tabIndex="5">
                            <option value="desc">Newest First</option>
                            <option value="asc">Oldest First</option>
                        </select>


                    </div>
                </div>

                <div className="flex justify-end">
                    <button className="btn my-3 w-36" type="submit" tabIndex="6">Search</button>
                </div>

            </form>


        </div>
    )
}