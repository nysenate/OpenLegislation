import React from 'react'
import 'app/icons.scss'
import {
  PublicCard,
  SubTitle,
  DataContainer,
  DataTypeIcon,
} from "../style"

const dataWeProvide = [
  {
    type: 'New York State Bills and Resolutions',
    blurb: 'Discover current and prior legislation that impacts New York State.',
    icon: 'icon-documents white',
    bgclass: 'blue3-bg',
    docsPage: 'bills.html'
  },
  {
    type: 'New York State Laws', blurb: 'Search through the current laws of NYS.',
    icon: 'icon-bookmarks white', bgclass: 'green3-bg', docsPage: 'laws.html'
  },
  {
    type: 'Senate Session/Hearing Transcripts', blurb: 'Records of Senate session floor discussion since 1993.',
    icon: 'icon-text white', bgclass: 'blue4-bg', docsPage: 'transcripts_floor.html'
  },
  {
    type: 'Senate Committee Agendas',
    blurb: 'Committee meetings to discuss bills and the votes to move them to the floor.',
    icon: 'icon-megaphone white',
    bgclass: 'green2-bg',
    docsPage: 'agendas.html'
  },
  {
    type: 'Senate Floor Calendars',
    blurb: 'Listings of bills that are scheduled for discussion and voting on the senate floor.',
    icon: 'icon-calendar white',
    bgclass: 'blue5-bg',
    docsPage: 'calendars.html'
  },
  {
    type: 'Senate/Assembly Membership', blurb: 'Senate and Assembly members for each session since 2009.',
    icon: 'icon-users white', bgclass: 'green1-bg', docsPage: 'members.html'
  }
];

export default function DataProvided() {

  return (
    <PublicCard>
      <header>
        <SubTitle>Data We Provide</SubTitle>
      </header>
      <DataContainer>
        <DataIcon icon={dataWeProvide[0].icon} bgClass='blue3-bg' />
        <SubTitle>{dataWeProvide[0].type}</SubTitle>
      </DataContainer>
      {/*<DataContainer>*/}
      {/*  {dataWeProvide.map((data) => {*/}
      {/*    return (*/}
      {/*      <div key={data.type} style={{width: '50%'}}>*/}
      {/*        <div>*/}
      {/*          <DataIcon icon={data.icon} bgColor={data.bgclass} />*/}
      {/*        </div>*/}
      {/*        /!*<div>*!/*/}
      {/*        /!*  <h3>*!/*/}
      {/*        /!*    {data.type}*!/*/}
      {/*        /!*  </h3>*!/*/}
      {/*        /!*  <p>{data.blurb}</p>*!/*/}
      {/*        /!*</div>*!/*/}
      {/*      </div>*/}
      {/*    )*/}
      {/*  })}*/}
      {/*</DataContainer>*/}
    </PublicCard>
  )
}

function DataIcon({ icon, bgClass }) {
  const classes = icon + ' ' + bgClass;
  return (
    <DataTypeIcon className={classes} />
  )
}
