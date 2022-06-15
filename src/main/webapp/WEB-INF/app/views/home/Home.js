import React from 'react';
import {
  Route,
  Switch,
  useHistory,
  useLocation,
} from "react-router-dom";
import Bills from "app/views/bills/Bills"
import Laws from "app/views/laws/Laws"
import Transcripts from "app/views/transcripts/Transcripts";
import Calendars from "app/views/calendars/Calendars"
import Agendas from "app/views/agendas/Agendas"
import { List, } from "phosphor-react";
import NavMenu from "app/views/home/NavMenu";
import PrivateRoute from "app/shared/PrivateRoute";
import Admin from "app/views/admin/Admin";
import NotFound from "app/views/NotFound";
import ApiUserInfo from "app/views/apiUser/ApiUserInfo";

const fakeHeaderText = "New York State Laws";

/**
 * Controls the header, left side nav menu, and main content block.
 * The left nav menu is displayed when the "2xl" breakpoint is reached, at smaller screen's it is hidden
 * in the hamburger menu.
 */
export default function Home() {
  const [ headerText, setHeaderText ] = React.useState(fakeHeaderText);
  const [ isMenuOpen, setMenuOpen ] = React.useState(false);
  const location = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    // Close the mobile menu whenever a link is clicked.
    setMenuOpen(false)
    // By default, direct users to the bill search page.
    if (location.pathname === "/") {
      history.push("/bills/search")
    }
  }, [ location ])

  return (
    <div className="bg-gray-100 h-auto w-screen">
      <Header isMenuOpen={isMenuOpen} setMenuOpen={setMenuOpen} headerText={headerText} />

      <div>
        <div>
          <NavMenu isMenuOpen={isMenuOpen} setMenuOpen={setMenuOpen} />
        </div>
        {isMenuOpen &&
          <div className="page-mask" onClick={() => setMenuOpen(false)} />
        }

        <div className="pl-0 2xl:pl-80 pt-16 md:min-h-screen">
          <Switch>
            <PrivateRoute path="/calendars">
              <Calendars setHeaderText={setHeaderText} />
            </PrivateRoute>
            <PrivateRoute path="/agendas">
              <Agendas setHeaderText={setHeaderText} />
            </PrivateRoute>
            <PrivateRoute path="/bills">
              <Bills setHeaderText={setHeaderText} />
            </PrivateRoute>
            <PrivateRoute path="/laws">
              <Laws setHeaderText={setHeaderText} />
            </PrivateRoute>
            <PrivateRoute path="/transcripts">
              <Transcripts setHeaderText={setHeaderText} />
            </PrivateRoute>
            <Route path="/subscriptions">
              <ApiUserInfo setHeaderText={setHeaderText} />
            </Route>
            <Route path="/admin">
              <Admin setHeaderText={setHeaderText} />
            </Route>
            <Route path="">
              <NotFound />
            </Route>
          </Switch>
        </div>
      </div>
    </div>
  )
}

function Header({ isMenuOpen, setMenuOpen, headerText }) {
  return (
    <div className="fixed h-16 w-full bg-blue-500 z-10">
      <div className="block 2xl:hidden">
        <MobileHeader isMenuOpen={isMenuOpen} setMenuOpen={setMenuOpen} headerText={headerText} />
      </div>

      <div className="hidden 2xl:block">
        <XLHeader headerText={headerText} />
      </div>
    </div>
  )
}

function MobileHeader({ isMenuOpen, setMenuOpen, headerText }) {
  return (
    <div className="flex h-16 items-center space-x-3 pl-3">
      <List color="white" size="2.5rem"
            onClick={() => setMenuOpen((isMenuOpen) => !isMenuOpen)} />
      <h1 className="h3 text-white">{headerText}</h1>
    </div>
  )
}

function XLHeader({ headerText }) {
  return (
    <div className="flex">
      <div className="flex w-80 h-16 bg-blue-500 items-center justify-center space-x-2">
        <img className="h-12" src="/static/img/nys_logo224x224.png" alt="Senate Seal" />
        <h1 className="h3 text-white">Open Legislation</h1>
      </div>
      <div className="flex items-center">
        <h2 className="ml-6 h4 text-white">{headerText}</h2>
      </div>
    </div>
  )
}
