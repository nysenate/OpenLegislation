import React from 'react'
import styled from 'styled-components'
import PublicHero from "./PublicHero";
import Card from "../common/Card"

const About = styled(Card)`
  position: relative;
  max-width: 96rem;
  padding: 5rem 3rem;
  margin: -10rem auto 0 auto;
  font-weight: 400;
  font-size: 1.9rem;
  text-align: center;
  
  & > a {
    color: #008cba;
  }
`

const BrowseCard = styled(Card)`
  margin: 5rem auto 0 auto;
  padding: 2rem 2rem;
  max-width: 96rem;
  
  & > header > h3 {
    margin: 0 auto 2rem 0;
    font-size: 2rem;
    text-align: center;
  }
  
  & > p {
    margin: 1rem 0;
    text-align: center;
    font-weight: 300;
    font-size: 1.9rem;
  }
`

const HomePage = styled.div`
  background: #f1f1f1;
  overflow: auto;
`

export default function Public() {
  return (
    <HomePage>
      <header>
        <PublicHero/>
      </header>
      <main>
        <About>
          Developed in-house at the <a href="http://www.nysenate.gov">New York State Senate</a>,
          Open Legislation is a public web service that provides access to NYS legislative data
          from the Legislative Bill Drafting Commission system through a JSON API.
        </About>
        <BrowseCard>
          <header>
            <h3>Browse Legislation</h3>
          </header>
          <p>
            We have an Open Legislation interface that we use to assist in our development.
          </p>
          <p>
            Feel free to access it by using your API Key.
          </p>
        </BrowseCard>
      </main>
    </HomePage>
  )
}
