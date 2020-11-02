import React from 'react'
import styled from 'styled-components'
import Header from "./Header"
import AboutCard from "./About"
import BrowseLegislation from "./BrowseLegislation"
import DataProvided from "./DataProvided"

export default function Public() {
  return (
    <HomePage>
      <Header />
      <main>
        <AboutCard />
        <BrowseLegislation />
        <DataProvided />
      </main>
    </HomePage>
  )
}

const HomePage = styled.div`
  background: #f1f1f1;
  overflow: auto;
`
