import React, { useEffect } from 'react';
import {
  Link,
  NavLink,
  Route,
  Switch,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import Bills from "app/views/bills/Bills"
import Laws from "app/views/laws"
import Transcripts from "app/views/transcripts/Transcripts";
import {
  CalendarBlank,
  List,
  IconContext,
  CaretDown,
  CaretUp,
  MagnifyingGlass,
  GitBranch,
  Megaphone,
  Files,
  Bookmarks,
  TextAlignLeft,
  Code,
  Article,
  SignOut,
} from "phosphor-react";
import NavMenu from "app/views/home/NavMenu";

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

  // Close the mobile menu whenever a link is clicked.
  React.useEffect(() => {
    setMenuOpen(false)
  }, [ location ])

  return (
    <div className="bg-gray-100 h-auto w-screen">
      <Header isMenuOpen={isMenuOpen} setMenuOpen={setMenuOpen} headerText={headerText} />

      <div>
        <div>
          <NavMenu isMenuOpen={isMenuOpen} setMenuOpen={setMenuOpen} />
        </div>

        <div className="pl-0 2xl:pl-80 pt-16 md:min-h-screen">
          <Switch>
            <Route path="/bills">
              <Bills setHeaderText={setHeaderText} />
            </Route>
            <Route path="/laws">
              <Laws setHeaderText={setHeaderText} />
            </Route>
            <Route path="/transcripts">
              <Transcripts />
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
