import React from 'react'
import PublicCard from "./PublicCard";
import SubTitle from "./SubTitle";


const dataWeProvide = [
  { type: 'New York State Bills and Resolutions', blurb: 'Discover current and prior legislation that impacts New York State.',
    icon: 'icon-documents', bgclass: 'blue3-bg', docsPage: 'bills.html'},
  { type: 'New York State Laws', blurb: 'Search through the current laws of NYS.',
    icon: 'icon-bookmarks', bgclass: 'green3-bg', docsPage: 'laws.html'},
  { type: 'Senate Session/Hearing Transcripts', blurb: 'Records of Senate session floor discussion since 1993.',
    icon: 'icon-text', bgclass: 'blue4-bg', docsPage: 'transcripts_floor.html'},
  { type: 'Senate Committee Agendas', blurb: 'Committee meetings to discuss bills and the votes to move them to the floor.',
    icon: 'icon-megaphone', bgclass: 'green2-bg', docsPage: 'agendas.html'},
  { type: 'Senate Floor Calendars', blurb: 'Listings of bills that are scheduled for discussion and voting on the senate floor.',
    icon: 'icon-calendar', bgclass: 'blue5-bg', docsPage: 'calendars.html'},
  { type: 'Senate/Assembly Membership', blurb: 'Senate and Assembly members for each session since 2009.',
    icon: 'icon-users', bgclass: 'green1-bg', docsPage: 'members.html'}
];

export default function DataProvided() {

  return (
    <PublicCard>
      <header>
        <SubTitle>Data We Provide</SubTitle>
      </header>
    </PublicCard>
  )
}