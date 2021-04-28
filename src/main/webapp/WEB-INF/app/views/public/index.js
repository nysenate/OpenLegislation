import React from 'react'
import BrowseLegislation from "./components/BrowseLegislation";
import DataProvided from "./components/DataProvided"
import SignUp from "./components/SignUp";
import OpenSource from "./components/OpenSource";
import Documentation from "./components/Documentation";
import Footer from "./components/Footer"
import {
  HomePage,
  PublicHeader,
  Title,
  SenateSealLogo,
  PublicWrapper,
  AboutCard,
} from "./style";

export default function PublicView() {
  return (
    <HomePage>
      <PublicHeader>
        <Title>
          <SenateSealLogo src="/static/img/nys_logo224x224.png"/>Open Legislation
        </Title>
      </PublicHeader>

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
    </HomePage>
  )
}


