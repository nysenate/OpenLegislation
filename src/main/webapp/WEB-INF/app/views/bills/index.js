import React, { useEffect } from 'react'
import {
  Route,
  Switch
} from "react-router-dom";
import Bill from "app/views/bills/Bill";
import Search from "app/views/bills/Search";

export default function Bills({ location, setTitle }) {

  useEffect(() => {
    if (location.pathname === '/bills') {
      setTitle("Search NYS Legislation")
    }
  }, [location]) // TODO title needs to be reset to this when back nav is used yet not override titles set by children.

  return (
    // TODO add these classes to a content-container class or component
    <div className="flex justify-center">
      <div className="w-full lg:w-10/12 bg-white">
        <Switch>
          <Route path="/bills/:sessionYear/:printNo" render={(props) => (
            <Bill {...props} setTitle={setTitle} />
          )} />
          <Route path="/bills" render={() => (
            <Search />
          )} />
        </Switch>
      </div>
    </div>
  )
}
