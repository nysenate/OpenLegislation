import React from 'react';
import lawSearchApi from "app/apis/lawSearchApi";
import getLawsApi from "app/apis/getLawsApi";
import {
    useLocation,
    useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import LawSearchForm from "app/views/laws/LawSearchForm";
import LawVolumeSearchResults from "app/views/laws/LawVolumeSearchResults";

export default function LawSearch() {
    const [response, setResponse] = React.useState({result: {items: []}})
    const [loading, setLoading] = React.useState(true)
    const location = useLocation()
    const history = useHistory()
    const params = queryString.parse(location.search)
    const limit = 6


    React.useEffect(() => {
        // search()
        doInitialSearch()
    }, [location])

    // Perform a search using the query string parameters.
    const search = () => {
        const params = queryString.parse(location.search)
        const page = params.page || 1
        const offset = (page - 1) * limit + 1
        const term = params.term || '*'
        const sort = params.sort
        let searchTerm = term

        doSearch(searchTerm, limit, offset, sort)
    }

    const doSearch = (term, limit, offset, sort) => {
        setLoading(true)
        lawSearchApi(term, limit, offset, sort)
            .then((response) => {
                setResponse(response)
            })
            .catch((error) => {
                // TODO properly handle errors
                console.warn(`${error}`)
            })
            .finally(() => {
                setLoading(false)
            })
    }

    const onPageChange = pageInfo => {
        params.page = pageInfo.selectedPage
        history.push({search: queryString.stringify(params)})
    }

    const doInitialSearch = () => {
        setLoading(true)
        getLawsApi(null, null)
            .then((response) => {
                setResponse(response)
            })
            .catch((error) => {
                // TODO properly handle errors
                console.warn(`${error}`)
            })
            .finally(() => {
                setLoading(false)
            })
    }

    return (
        <div className="p-3">
            <LawSearchForm searchTerm={params.term}/>
            {loading
                ? <LoadingIndicator/>
                : <LawVolumeSearchResults response={response}
                                          limit={limit}
                                          page={params.page}
                                          onPageChange={onPageChange}/>
            }
        </div>
    )
}

