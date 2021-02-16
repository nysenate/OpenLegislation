import React from 'react'
import DataProvided from "./components/DataProvided"
import {
  HomePage,
  PublicHeader,
  Title,
  SenateSealLogo,
  AboutCard,
  PublicCard,
  SubTitle,
  ApiKeyFormContainer,
  Button,
} from "./style";

export default function PublicView() {
  const apiKeyRef = React.useRef();

  return (
    <HomePage>
      <PublicHeader>
        <Title>
          <SenateSealLogo src="/static/img/nys_logo224x224.png"/>
        </Title>
      </PublicHeader>

      <main>
        <AboutCard>
          Developed in-house at the <a href="http://www.nysenate.gov">New York State Senate</a>,
          Open Legislation is a public web service that provides access to NYS legislative data
          from the Legislative Bill Drafting Commission system through a JSON API.
        </AboutCard>

        <PublicCard>
          <header>
            <SubTitle>Browse Legislation</SubTitle>
          </header>
          <p>
            We have an Open Legislation interface that we use to assist in our development.
          </p>
          <p>
            Feel free to access it by using your API Key.
          </p>

          <ApiKeyFormContainer>
            <form onSubmit={() => alert(apiKeyRef.current.value)}>
              <input
                ref={apiKeyRef}
                type="text"
                name="apiKey"
                placeholder="Enter your API key here to browse the data."/>
              <Button type="submit">View Legislation</Button>
            </form>
          </ApiKeyFormContainer>
        </PublicCard>

        <DataProvided/>

      </main>
    </HomePage>
  )
}


