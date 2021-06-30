import React from 'react';

const fakeTitle = "New York State Laws";

export default function LegislationView({children}) {

  const [title, setTitle] = React.useState(fakeTitle);
  const [open, setOpen] = React.useState(false);

  return (
    <div className="bg-gray-100 h-screen w-screen">
      <Header title={title} />

      <div className="">
        <div>
          <Menu open={open}/>
        </div>
        <div className="pl-0 xl:pl-80">
          content here
          {open && " Open"}
        </div>
      </div>
    </div>
  )
}

function Header({ title }) {
  return (
    <div className="h-16 w-full bg-blue-500">
      <div className="block xl:hidden">
        <MobileHeader title={title} />
      </div>

      <div className="hidden xl:block">
        <XLHeader title={title} />
      </div>
    </div>
  )
}

function MobileHeader({ title }) {
  return (
    <div className="flex h-16 items-center space-x-3 pl-3">
      <i className="icon-menu text-5xl text-white cursor-pointer"
         onClick={() => setOpen((open) => !open)}/>
      <h1 className="h3 text-white">{title}</h1>
    </div>
  )
}

function XLHeader({ title }) {
  return (
    <div className="flex">
      <div className="flex w-80 h-16 bg-blue-500 items-center justify-center space-x-2">
        <img className="h-12" src="/static/img/nys_logo224x224.png" alt="Senate Seal"/>
        <h1 className="h3 text-white">Open Legislation</h1>
      </div>
      <div className="flex items-center">
        <h2 className="ml-8 h4 text-white">{title}</h2>
      </div>
    </div>
  )
}


function Menu({open}) {
  /*
   * If the viewport is < xl, open the menu when open = true, hide when open = false.
   * If the viewport is > xl, always show the menu regardless of the value of open.
   */
  let className = "absolute transform bg-gray-200 w-80 h-full";
  if (open) {
    className += " translate-x-0 xl:translate-x-0"
  } else {
    className += " -translate-x-80 xl:translate-x-0"
  }

  return (
    <div className={className}>
      <ul>
        <MenuItem>Senate Calendars</MenuItem>
        <MenuItem>Senate Agendas / Meetings</MenuItem>
        <MenuItem>Bills and Resolutions</MenuItem>
        <MenuItem>New York State Laws</MenuItem>
        <MenuItem>Session / Hearing Transcripts</MenuItem>
        <MenuItem>JSON API Docs</MenuItem>
        <MenuItem>Logout</MenuItem>
      </ul>
    </div>
  )
}

function MenuItem({children}) {
  return (
    <li className="p-3">
      {children}
    </li>
  )
}
