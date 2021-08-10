import React from 'react'
import {
    useHistory,
    useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import { Sliders } from "phosphor-react";

const advancedSearchTitleEls = (
    <div className="flex items-center">
        <Sliders size="1.25rem" className="inline-block mr-2" />
        <h4 className="inline-block">Advanced Search Options</h4>
    </div>
)

export default function LawSearchForm() {
    const [ term, setTerm ] = React.useState("")
    const location = useLocation()
    const history = useHistory()

    // Update search fields when back/forward navigation is used.
    React.useEffect(() => {
        const params = queryString.parse(location.search)
        setTerm(params.term || "")
    }, [ location ])

    // Updates the term query param when the form is submitted.
    const onSubmit = (e) => {
        e.preventDefault()
        const params = queryString.parse(location.search)
        params.term = term
        console.log(params)
        history.push({ search: queryString.stringify(params) })
    }

    return (
        <div>
            <form onSubmit={onSubmit}>
                <div className="flex flex-wrap">
                    <div className="flex-grow mr-8">
                        <label htmlFor="lawsearch" className="label label--top">
                            Search for Laws
                        </label>
                        <input onChange={(e) => setTerm(e.target.value)}
                               value={term}
                               tabIndex="1"
                               name="lawsearch"
                               type="text"
                               className="input w-full"
                               placeholder="e.g. official state muffin , STL 84" />
                    </div>
                </div>

                    <div className="flex justify-end">
                        <button className="btn my-3 w-36" type="submit" tabIndex="2">Search</button>
                    </div>

                <div className="flex flex-wrap">
                    <div className="flex-grow mr-8">
                        <label htmlFor="lawvolumesearch" className="label label--top">
                            Browse By Law Volume
                        </label>
                        <input onChange={(e) => setTerm(e.target.value)}
                               value={term}
                               tabIndex="3"
                               name="lawvolumesearch"
                               type="text"
                               className="input w-half"
                               placeholder="e.g. TAX" />
                    </div>
                </div>

                <div className="flex justify-left">
                    <button className="btn my-3 w-36" type="submit" tabIndex="4">Search</button>
                </div>
            </form>
        </div>
    )
}