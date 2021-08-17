import React from 'react'
import {
    useHistory,
    useLocation
} from "react-router-dom";
import * as queryString from "query-string";

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
                        <label htmlFor="specifiedlawsearch" className="label label--top">
                            Navigate by section number
                        </label>
                        <input onChange={(e) => setTerm(e.target.value)}
                               value={term}
                               tabIndex="1"
                               name="specifiedlawsearch"
                               type="text"
                               className="input w-full"
                               placeholder="e.g. 32.01" />
                    </div>
                </div>

                <div className="flex justify-end">
                    <button className="btn my-3 w-36" type="submit" tabIndex="2">Search</button>
                </div>
            </form>
        </div>
    )
}