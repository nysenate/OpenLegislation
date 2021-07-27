import React, { useEffect } from 'react'
import {
  Route,
  Switch,
  useLocation
} from "react-router-dom";
import Bill from "app/views/bills/Bill";
import Search from "app/views/bills/Search";
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
          <Bill setHeaderText={setHeaderText} />
        </Route>
        <Route path="/bills">
          <Search />
        </Route>
      </Switch>
    </ContentContainer>
  )
}
