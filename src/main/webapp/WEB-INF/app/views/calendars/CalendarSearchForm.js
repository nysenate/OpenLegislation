import React from 'react'

export default function CalendarSearchForm({updateValues}) {

    React.useEffect(() => {
    }, [])

    // Updates the term query param when the form is submitted.
    const onSubmitCalendarSearch = (e) => {
        e.preventDefault()

        const year = document.getElementById("year")
        console.log(year.value)

        const searchBy = document.getElementById("searchBy")
        console.log(searchBy.value)

        const searchValue = document.getElementById("searchValue")
        console.log(searchValue.value)

        const sortSelect = document.getElementById("sort")
        console.log(sortSelect.value)

        const activeListOnly = document.getElementById("activeListOnly")
        console.log(activeListOnly.checked)

        updateValues(year.value, searchBy.value, searchValue.value, sortSelect.value, activeListOnly.checked)
    }

    return (
        <div>

            <form onSubmit={onSubmitCalendarSearch}>
                <div className="flex flex-wrap">
                    <div className="flex-grow mr-8">

                        <h1 className="p-5 font-bold">Field Search</h1>

                        <label htmlFor="year" className="label label--left uppercase font-bold p-2">Year:</label>
                        <select className="pl-4" name="year" id="year" tabIndex="1">
                            <option value="2021">2021</option>
                            <option value="allyears">All Years</option>
                            <option value="2022">2022</option>
                            <option value="2020">2020</option>
                            <option value="2019">2019</option>
                            <option value="2018">2018</option>
                            <option value="2017">2017</option>
                            <option value="2016">2016</option>
                            <option value="2015">2015</option>
                            <option value="2014">2014</option>
                            <option value="2013">2013</option>
                            <option value="2012">2012</option>
                            <option value="2011">2011</option>
                            <option value="2010">2010</option>
                            <option value="2009">2009</option>
                        </select>

                        <label htmlFor="with" className="label label--left uppercase font-bold p-2">With:</label>
                        <select className="pl-4" name="searchBy" id="searchBy" tabIndex="2">
                            <option value="calDate">Calendar No.</option>
                            <option value="printNo">Print No.</option>
                            <option value="billCalNo">Bill Calendar No.</option>
                        </select>

                        <label htmlFor="searchValue" className="label label--left uppercase font-bold p-2">Search Value:</label>
                        <input className="pl-4" type="text" id="searchValue" name="searchValue" tabIndex="3"/>

                        <label htmlFor="sort" className="label label--left uppercase font-bold p-2">Sort:</label>
                        <select className="pl-4" name="sort" id="sort" tabIndex="4">
                            <option value="desc">Newest First</option>
                            <option value="asc">Oldest First</option>
                        </select>

                        <label htmlFor="sort" className="label label--left uppercase font-bold p-2">Active List Only:</label>
                        <input type="checkbox" id="activeListOnly" name="activeListOnly" tabIndex="5"/>


                    </div>
                </div>

                <div className="flex justify-end">
                    <button className="btn my-3 w-36" type="submit" tabIndex="6">Search</button>
                </div>

            </form>


        </div>
    )
}