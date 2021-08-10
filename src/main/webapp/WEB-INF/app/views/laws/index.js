import React, { useEffect } from 'react'
import {
    Route,
    Switch,
    useLocation
} from "react-router-dom";
// import Bill from "app/views/bills/Bill";
import LawSearch from "app/views/laws/LawSearch";
import ContentContainer from "app/shared/ContentContainer";

export default function Laws({ setHeaderText }) {
    const location = useLocation()

    useEffect(() => {
        if (location.pathname === '/laws') {
            setHeaderText("NYS Laws")
        }
    }, [ location ])

    return (
        <ContentContainer>
            <Switch>
                <Route path="/laws">
                    <LawSearch />
                </Route>
            </Switch>
        </ContentContainer>
    )
}