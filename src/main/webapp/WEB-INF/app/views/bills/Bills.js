import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";
import Bill from "app/views/bills/info/Bill";
import BillSearch from "app/views/bills/search/BillSearch";
import ContentContainer from "app/shared/ContentContainer";
import BillUpdates from "app/views/bills/updates/BillUpdates";

export default function Bills({ setHeaderText }) {
  const location = useLocation()

  useEffect(() => {
    if (location.pathname.startsWith('/bills')) {
      setHeaderText("Search NYS Legislation")
    }
  }, [ location ])

  return (
    <ContentContainer>
      <Switch>
        <Route path="/bills/:sessionYear/:printNo">
          <Bill setHeaderText={setHeaderText} />
        </Route>
        <Route path="/bills/updates">
          <BillUpdates />
        </Route>
        <Route path="/bills">
          <BillSearch />
        </Route>
      </Switch>
    </ContentContainer>
  )
}
