import React from 'react'
import {
    useHistory,
    useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import Input from "app/shared/Input";


export default function LawSearchForm({handleVolumeSearchFilter, handleSearchTerm}) {
    const [ term, setTerm ] = React.useState("")
    const [ filter, setFilter ] = React.useState("")
    const location = useLocation()
    const history = useHistory()

    // Update search fields when back/forward navigation is used.
    React.useEffect(() => {
        const params = queryString.parse(location.search)
        setTerm(params.term || "")
    }, [ location ])

    // Updates the term query param when the form is submitted.
    const onSubmitLawSearch = (e) => {
        e.preventDefault()
        const input = document.getElementById("lawsearch");
        const params = queryString.parse(location.search)
        params.term = term
        history.push({ search: queryString.stringify(params) })
        handleSearchTerm(term)
    }

    const onSubmitLawVolumeSearch = (e) => {
        e.preventDefault()
        const input = document.getElementById("lawvolumesearch")
        console.log(input.value)
        handleVolumeSearchFilter(input.value)
    }

    return (
        <div>
            <form onSubmit={onSubmitLawSearch}>
                <div className="flex flex-wrap">
                    <div className="flex-grow mr-8">
                        <Input label="Search for Laws"
                               onChange={(e) => setTerm(e.target.value)}
                               value={term}
                               tabIndex="1"
                               name="lawsearch"
                               type="text"
                               placeholder="e.g. official state muffin , STL 84"/>
                    </div>
                </div>

                    <div className="flex justify-end">
                        <button className="btn my-3 w-36" type="submit" tabIndex="2">Search</button>
                    </div>
            </form>

            <form onSubmit={onSubmitLawVolumeSearch}>
                <div className="flex w-2/12">
                    <Input label="Browse By Law Volume"
                           onChange={(e) => setFilter(e.target.value)}
                           value={filter}
                           tabIndex="3"
                           name="lawvolumesearch"
                           type="text"
                           placeholder="e.g. TAX" />
                </div>

                <div className="flex justify-left">
                    <button className="btn my-3 w-36" type="submit" tabIndex="4">Search</button>
                </div>
            </form>

        </div>
    )
}