import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";
import BillInfo from "app/views/bills/info/BillInfo";
import BillSearch from "app/views/bills/search/BillSearch";
import ContentContainer from "app/shared/ContentContainer";

export default function Bills({ setHeaderText }) {
  const location = useLocation()

  useEffect(() => {
    if (location.pathname === '/bills') {
      setHeaderText("Search NYS Legislation")
    }
  }, [ location ])

  return (
    <ContentContainer>
      <Switch>
        <Route path="/bills/:sessionYear/:printNo">
          <BillInfo setHeaderText={setHeaderText} />
        </Route>
        <Route path="/bills">
          <BillSearch />
        </Route>
      </Switch>
    </ContentContainer>
  )
}
