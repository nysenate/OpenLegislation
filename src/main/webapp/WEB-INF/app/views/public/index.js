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
  Paragraph,
  Input,
  ApiKeyInput,
  SignUpInput,
  TwoColumns,
  DocsIframe,
} from "./style";

export default function PublicView() {
  const apiKeyRef = React.useRef();
  const name = React.useRef();
  const email = React.useRef();

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
              <ApiKeyInput
                ref={apiKeyRef}
                type="text"
                name="apiKey"
                placeholder="Enter your API key here to browse the data."/>
              <Button type="submit">View Legislation</Button>
            </form>
          </ApiKeyFormContainer>
        </PublicCard>

        <DataProvided/>

        <PublicCard>
          <header>
            <SubTitle>Sign up for an API Key</SubTitle>
          </header>
          <p>
            By signing up for a key you can access the API to power your own legislative apps.
            Simply provide a name and a valid email and we'll send over the credentials.
          </p>
          <form onSubmit={() => alert("hello")}>
            <TwoColumns>
              <SignUpInput
                ref={name}
                type="text"
                name="name"
                placeholder="Name"/>
              <SignUpInput
                ref={email}
                type="text"
                name="email"
                placeholder="Email"/>
              <Input
                type="checkbox"
                name="breakingChanges"
                defaultChecked/>
              <label htmlFor="breakingChanges">Sign up for emails</label>
              <Input
                type="checkbox"
                name="newFeatures"
                defaultUnChecked/>
              <label htmlFor="newFeatures">Sign up for new features</label>
            </TwoColumns>
            <Button type="submit">Get API Key</Button>
          </form>
        </PublicCard>

        <PublicCard>
          <header>
            <SubTitle>Open Source</SubTitle>
          </header>
          <p>
            OpenLegislation is developed using several open-source packages and frameworks.
          </p>
          <p>
            Source code <a href="http://www.github.com/nysenate/OpenLegislation">is published on GitHub</a>.
            Feel free to open any tickets with issues you are having or contact the development
            team at <a href="mailto:senatedev@nysenate.gov">senatedev@nysenate.gov</a>.
          </p>
        </PublicCard>

        <PublicCard>
          <header>
            <SubTitle>How to use the API</SubTitle>
          </header>
          <a target="_blank" href="/docs">View docs in new window</a>
          <DocsIframe width="100%" className="docs-iframe" src="/static/docs/html/index.html"/>
        </PublicCard>

        <div>
          <img src="//licensebuttons.net/l/by-nc-nd/3.0/us/88x31.png"/>
          <p>
            This content is licensed under Creative Commons BY-NC-ND 3.0.
            The software and services provided under this site are offered under the BSD License and the GPL v3 License.
          </p>
        </div>
      </main>
    </HomePage>
  )
}


