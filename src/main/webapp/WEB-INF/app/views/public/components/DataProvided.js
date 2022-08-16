import React from 'react'
import {
  Bookmarks,
  CalendarBlank,
  Files,
  IconContext,
  Megaphone,
  TextAlignLeft,
  UsersThree
} from "phosphor-react";

const dataWeProvide = [
  {
    type: 'New York State Bills and Resolutions',
    blurb: 'Discover current and prior legislation that impacts New York State.',
    icon: (<Files />),
    bgclass: 'bg-blue-300',
    docsPage: 'bills.html'
  },
  {
    type: 'New York State Laws',
    blurb: 'Search through the current laws of NYS.',
    icon: (<Bookmarks />),
    bgclass: 'bg-green-400',
    docsPage: 'laws.html'
  },
  {
    type: 'Senate Session/Hearing Transcripts',
    blurb: 'Records of Senate session floor discussion since 1993.',
    icon: (<TextAlignLeft />),
    bgclass: 'bg-blue-500',
    docsPage: 'transcripts_floor.html'
  },
  {
    type: 'Senate Committee Agendas',
    blurb: 'Committee meetings to discuss bills and the votes to move them to the floor.',
    icon: (<Megaphone />),
    bgclass: 'bg-green-500',
    docsPage: 'agendas.html'
  },
  {
    type: 'Senate Floor Calendars',
    blurb: 'Listings of bills that are scheduled for discussion and voting on the senate floor.',
    icon: (<CalendarBlank />),
    bgclass: 'bg-blue-700',
    docsPage: 'calendars.html'
  },
  {
    type: 'Senate/Assembly Membership',
    blurb: 'Senate and Assembly members for each session since 2009.',
    icon: (<UsersThree />),
    bgclass: 'bg-green-700',
    docsPage: 'members.html'
  }
];

export default function DataProvided() {

  function createDataCard(index) {
    return (
      <a href={`/static/docs/html/${dataWeProvide[index].docsPage}`} className="w-full lg:w-6/12">
        <div className="p-4 flex justify-start cursor-pointer text-white text-left hover:bg-gray-200 transition duration-300 rounded">
          <div className={`flex justify-center items-center ${dataWeProvide[index].bgclass} p-3 mr-3`} style={{height: 68}}>
            {dataWeProvide[index].icon}
          </div>
          <div>
            <h3 className="h5">{dataWeProvide[index].type}</h3>
            <p className="text text--small mt-1">{dataWeProvide[index].blurb}</p>
          </div>
        </div>
      </a>
    )
  }

  return (
    <section className="card mt-12 text-center">
      <header>
        <h2 className="h3">Data We Provide</h2>
      </header>
      <div className="mt-2 flex flex-row flex-wrap">
        <IconContext.Provider
          value={{
            color: "white",
            size: '2rem',
            weight: "regular",
          }}
        >
          {createDataCard(0)}
          {createDataCard(1)}
          {createDataCard(2)}
          {createDataCard(3)}
          {createDataCard(4)}
          {createDataCard(5)}
        </IconContext.Provider>
      </div>
    </section>
  )
}
