import React from 'react'
import 'app/icons.scss'
import theme from 'app/Theme';
import {
  PublicCard,
  SubTitle,
  TitleSmall,
  DataProvidedContainer,
  DataProvidedListItem,
  DataTypeIcon,
} from "../style"

const dataWeProvide = [
  {
    type: 'New York State Bills and Resolutions',
    blurb: 'Discover current and prior legislation that impacts New York State.',
    icon: 'icon-documents white',
    bgclass: theme.colors.blue5,
    docsPage: 'bills.html'
  },
  {
    type: 'New York State Laws',
    blurb: 'Search through the current laws of NYS.',
    icon: 'icon-bookmarks white',
    bgclass: theme.colors.green3,
    docsPage: 'laws.html'
  },
  {
    type: 'Senate Session/Hearing Transcripts',
    blurb: 'Records of Senate session floor discussion since 1993.',
    icon: 'icon-text white',
    bgclass: theme.colors.blue7,
    docsPage: 'transcripts_floor.html'
  },
  {
    type: 'Senate Committee Agendas',
    blurb: 'Committee meetings to discuss bills and the votes to move them to the floor.',
    icon: 'icon-megaphone white',
    bgclass: theme.colors.green5,
    docsPage: 'agendas.html'
  },
  {
    type: 'Senate Floor Calendars',
    blurb: 'Listings of bills that are scheduled for discussion and voting on the senate floor.',
    icon: 'icon-calendar white',
    bgclass: theme.colors.blue9,
    docsPage: 'calendars.html'
  },
  {
    type: 'Senate/Assembly Membership',
    blurb: 'Senate and Assembly members for each session since 2009.',
    icon: 'icon-users white',
    bgclass: theme.colors.green7,
    docsPage: 'members.html'
  }
];

export default function DataProvided() {

  function createDataCard(index) {
    return (
      <DataProvidedListItem>
        <DataIcon icon={dataWeProvide[index].icon} bgClass={dataWeProvide[index].bgclass}/>
        <div>
          <TitleSmall>{dataWeProvide[index].type}</TitleSmall>
          <p>{dataWeProvide[index].blurb}</p>
        </div>
      </DataProvidedListItem>)
  }

  return (
    <PublicCard>
      <header>
        <SubTitle>Data We Provide</SubTitle>
      </header>
      <DataProvidedContainer>
        {createDataCard(0)}
        {createDataCard(1)}
        {createDataCard(2)}
        {createDataCard(3)}
        {createDataCard(4)}
        {createDataCard(5)}
      </DataProvidedContainer>
    </PublicCard>
  )
}

function DataIcon({icon, bgClass}) {
  const classes = icon;
  return (
    <DataTypeIcon className={classes} bgColor={bgClass}/>
  )
}
