import React, { useEffect } from 'react';
import {
  BrowserRouter as Router,
  NavLink,
  Route,
  Switch
} from "react-router-dom";
import Bills from "app/views/bills"

const fakeHeaderText = "New York State Laws";

export default function LegislationView() {

  const [ headerText, setHeaderText ] = React.useState(fakeHeaderText);
  const [ isMenuOpen, setMenuOpen ] = React.useState(false);

  return (
    <Router>
      <div className="bg-gray-100 h-auto w-screen">
        <Header isMenuOpen={isMenuOpen} setMenuOpen={setMenuOpen} headerText={headerText} />

        <div>
          <div>
            <Menu isMenuOpen={isMenuOpen} />
          </div>
          <div className="pl-0 xl:pl-80 pt-16 md:min-h-screen">
            <Switch>
              <Route path="/bills" render={(props) => (
                <Bills {... props} setHeaderText={setHeaderText} />
              )}
              />
            </Switch>
          </div>
        </div>
      </div>
    </Router>
  )
}

function Header({ isMenuOpen, setMenuOpen, headerText }) {
  return (
    <div className="fixed h-16 w-full bg-blue-500 z-10">
      <div className="block xl:hidden">
        <MobileHeader isMenuOpen={isMenuOpen} setMenuOpen={setMenuOpen} headerText={headerText} />
      </div>

      <div className="hidden xl:block">
        <XLHeader headerText={headerText} />
      </div>
    </div>
  )
}

function MobileHeader({ isMenuOpen, setMenuOpen, headerText }) {
  return (
    <div className="flex h-16 items-center space-x-3 pl-3">
      <i className="icon-menu text-5xl text-white cursor-pointer"
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


function Menu({ isMenuOpen }) {
  /*
   * If the viewport is < xl, open the menu when open = true, hide when open = false.
   * If the viewport is > xl, always show the menu regardless of the value of open.
   */
  let className = "fixed top-16 transform bg-gray-200 w-80 h-full z-10";
  if (isMenuOpen) {
    className += " translate-x-0 xl:translate-x-0"
  } else {
    className += " -translate-x-80 xl:translate-x-0"
  }

  return (
    <div className={className}>
      <ul>
        <MenuItem to="/calendars">Senate Calendars</MenuItem>
        <MenuItem to="/agendas">Senate Agendas / Meetings</MenuItem>
        <MenuItem to="/bills">Bills and Resolutions</MenuItem>
        <MenuItem to="/laws">New York State Laws</MenuItem>
        <MenuItem to="/transcripts">Session / Hearing Transcripts</MenuItem>
        <MenuItem to="/docs">JSON API Docs</MenuItem>
        <MenuItem to="/logout">Logout</MenuItem>
      </ul>
    </div>
  )
}

function MenuItem({ to, children }) {
  return (
    <li className="p-3">
      <NavLink to={to} activeClassName="bg-blue-200">
        {children}
      </NavLink>
    </li>
  )
}
