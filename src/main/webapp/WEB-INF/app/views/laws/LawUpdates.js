import React from 'react';
import {
    useLocation,
    useHistory
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import LawUpdatesSearchForm from "app/views/laws/LawUpdatesSearchForm";
///api/3/laws/updates/{from date-time}/{to date-time}

export default function LawSearch() {
    const [loading, setLoading] = React.useState(false)
    const [searching, setSearching] = React.useState(true)
    const location = useLocation()
    const history = useHistory()
    const params = queryString.parse(location.search)
    const limit = 6


    React.useEffect(() => {

    }, [])



    return (
        <div className="p-3">
            {loading
                ? <LoadingIndicator/>
                :
                <div>
                    <LawUpdatesSearchForm/>
                </div>
            }
        </div>
    )

    function isEmpty(str) {
        return (!str || str.length === 0 );
    }
}

