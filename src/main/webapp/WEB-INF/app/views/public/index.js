import React from 'react'
import styled from 'styled-components'
import BrowseLegislation from "./components/BrowseLegislation";
import DataProvided from "./components/DataProvided"
import SignUp from "./components/SignUp";
import OpenSource from "./components/OpenSource";
import Documentation from "./components/Documentation";
import Footer from "./components/Footer"
import PageWrapper from "../../shared/PageWrapper"
import PublicHeader from "./PublicHeader";
import {Card} from "app/shared/Card";

export default function PublicView() {
  return (
    <PageWrapper>
      <PublicHeader />
      <main>
        <PublicWrapper>
          <AboutCard>
            <p>
            Developed in-house at the <a href="http://www.nysenate.gov">New York State Senate</a>,
            Open Legislation is a public web service that provides access to NYS legislative data
            from the Legislative Bill Drafting Commission system through a JSON API.
            </p>
          </AboutCard>

          <BrowseLegislation />
          <DataProvided/>
          <SignUp />
          <OpenSource />
          <Documentation />
          <Footer />
        </PublicWrapper>
      </main>
    </PageWrapper>
  )
}

export const PublicWrapper = styled.div`
  margin: 0 auto;
  max-width: 960px;
`

const AboutCard = styled(Card)`
  padding: 16px 32px;
  position: relative;
  margin: -100px auto 0 auto;
  font-size: 20px;
  text-align: center;
`
