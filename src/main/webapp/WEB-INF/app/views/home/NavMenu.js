import React from "react"
import {
  Article,
  Bookmarks,
  CalendarBlank,
  CaretDown,
  CaretUp,
  Code,
  Files,
  GitBranch,
  IconContext,
  MagnifyingGlass,
  Megaphone,
  SignOut,
  Sliders,
  TextAlignLeft
} from "phosphor-react";
import {
  Link,
  useLocation
} from "react-router-dom";
import useAuth from "app/shared/useAuth";

export default function NavMenu({ isMenuOpen }) {
  /*
   * If the viewport is < xl, open the menu when open = true, hide when open = false.
   * If the viewport is > xl, always show the menu regardless of the value of open.
   */
  let className = "fixed top-16 transform bg-gray-200 w-80 h-full z-20 overflow-y-auto";
  if (isMenuOpen) {
    className += " translate-x-0 2xl:translate-x-0"
  } else {
    className += " -translate-x-80 2xl:translate-x-0"
  }

  return (
    <div className={className}>
      <MenuContent />
    </div>
  )
}

function MenuContent() {
  const [ openCategories, setOpenCategories ] = React.useState([])
  const location = useLocation()

  /**
   * Opens the category for the current url on page load.
   * Also closes all non active categories when a link is clicked.
   */
  React.useEffect(() => {
    if (activeCategory()) {
      setOpenCategories([ activeCategory().name ])
    }
  }, [ location.pathname ])

  /**
   * Adds or removes a category from the array of openCategories
   * User is not allowed to close the currently active category.
   * @param name
   */
  const toggleCategory = (name) => {
    if (name === activeCategory()?.name) {
      return // never collapse the active category.
    }

    if (openCategories.includes(name)) {
      setOpenCategories((openCategories) => {
        return openCategories.filter((cat) => cat !== name)
      })
    } else {
      setOpenCategories((openCategories) => {
        return openCategories.concat([ name ])
      })
    }
  }

  /**
   * Returns the category which is currently being browsed.
   */
  const activeCategory = () => {
    return navCategories.find((category) => location.pathname.startsWith(category.path))
  }

  return (
    <div>
      <IconContext.Provider
        value={{
          size: "1.3rem",
          weight: "regular",
        }}>
        <ul>
          {navCategories.map((opt) => {
            return (
              <NavCategory {...opt}
                           isOpen={openCategories.includes(opt.name)}
                           isActive={activeCategory()?.name === opt.name}
                           onCategoryClick={() => toggleCategory(opt.name)}
                           key={opt.name} />
            )
          })}
          <AdminPage name="Admin" icon={<Sliders />} to="/admin/index" />
          <DocPage name="JSON API Docs" icon={<Code />} to="/static/docs/html/index.html" />
          <LogoutPage name="Logout" icon={<SignOut />} to="/logout" />
        </ul>
      </IconContext.Provider>
    </div>
  )
}

function NavCategory({ name, icon, isOpen, isActive, onCategoryClick, children }) {
  let navCategoryClass = "py-4 flex items-center transition-colors"
  navCategoryClass += isOpen ? " bg-blue-500 text-white" : " hover:bg-blue-700 hover:text-white"
  navCategoryClass += isActive ? "" : " cursor-pointer"
  return (
    <div className="border-b-1 border-gray-300" tabIndex="-1">
      <div className={navCategoryClass} onClick={onCategoryClick}>
        <div className="ml-5 mr-2 inline">{icon}</div>
        <div className="flex-grow">{name}</div>
        {isOpen ? <CaretUp className="mr-3" /> : <CaretDown className="mr-3" />}
      </div>

      {isOpen &&
        <div>
          {children}
        </div>
      }
    </div>
  )
}

function NavChild({ name, to, icon }) {
  let childClassNames = `py-3 pl-16 pr-3 text-sm flex items-center space-x-3
  text-gray-700 hover:bg-blue-700 hover:text-white transition-colors`

  return (
    <Link to={to} tabIndex="-1">
      <div className={childClassNames}>
        <div>
          {icon}
        </div>
        <div>
          {name}
        </div>
      </div>
    </Link>
  )
}

function LogoutPage({ name, icon, to }) {
  return (
    <Link to={to} tabIndex="-1">
      <div className="py-4 flex items-center border-b-1 border-gray-300 cursor-pointer text-gray-700 hover:bg-blue-700 hover:text-white">
        <div className="ml-5 mr-2 inline">{icon}</div>
        <div className="flex-grow">{name}</div>
      </div>
    </Link>
  )
}

function DocPage({ name, icon, to }) {
  return (
    <a href={to} tabIndex="-1">
      <div className="py-4 flex items-center border-b-1 border-gray-300 cursor-pointer text-gray-700 hover:bg-blue-700 hover:text-white">
        <div className="ml-5 mr-2 inline">{icon}</div>
        <div className="flex-grow">{name}</div>
      </div>
    </a>
  )
}

function AdminPage({ name, icon, to }) {
  const auth = useAuth()

  if (!auth.isAdmin()) {
    return null
  }

  if (auth.isAdmin()) {
    return (
      <Link to={to} tabIndex="-1">
        <div className="py-4 flex items-center border-b-1 border-gray-300 cursor-pointer text-gray-700 hover:bg-blue-700 hover:text-white">
          <div className="ml-5 mr-2 inline">{icon}</div>
          <div className="flex-grow">{name}</div>
        </div>
      </Link>
    )
  }
}

const navCategories = [
  {
    name: "Senate Calendars",
    path: "/calendars",
    icon: <CalendarBlank />,
    children: (
      <React.Fragment>
        <NavChild name="Search Calendars" icon={<MagnifyingGlass />} to="/calendars/search" />
        <NavChild name="Calendar Updates" icon={<GitBranch />} to="/calendars/updates" />
      </React.Fragment>
    )
  },
  {
    name: "Senate Agendas",
    path: "/agendas",
    icon: <Megaphone />,
    children: (
      <React.Fragment>
        <NavChild name="Search Agendas" icon={<MagnifyingGlass />} to="/agendas/search" />
        <NavChild name="Agenda Updates" icon={<GitBranch />} to="/agendas/updates" />
      </React.Fragment>
    )
  },
  {
    name: "Bills and Resolutions",
    path: "/bills",
    icon: <Files />,
    children: (
      <React.Fragment>
        <NavChild name="Search Bills" icon={<MagnifyingGlass />} to="/bills/search" />
        <NavChild name="Bill Updates" icon={<GitBranch />} to="/bills/updates" />
      </React.Fragment>
    )
  },
  {
    name: "New York State Laws",
    path: "/laws",
    icon: <Bookmarks />,
    children: (
      <React.Fragment>
        <NavChild name="Search Laws" icon={<MagnifyingGlass />} to="/laws" />
        <NavChild name="Law Updates" icon={<GitBranch />} to="/laws/updates" />
      </React.Fragment>
    )
  },
  {
    name: "Senate Transcripts",
    path: "/transcripts",
    icon: <TextAlignLeft />,
    children: (
      <React.Fragment>
        <NavChild name="Session Transcripts" icon={<TextAlignLeft />} to="/transcripts/session" />
        <NavChild name="Public Hearing Transcripts" icon={<Article />} to="/transcripts/hearing" />
      </React.Fragment>
    )
  },
]
