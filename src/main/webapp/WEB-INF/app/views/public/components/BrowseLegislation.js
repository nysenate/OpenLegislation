import React from 'react'
import {
  ApiKeyFormContainer,
  Input,
  Button,
  PublicCard,
  SubTitle
} from "../style"

export default function BrowseLegislation() {
  const apiKeyRef = React.useRef();

  return (
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
          <Input
            width="500px"
            ref={apiKeyRef}
            type="text"
            name="apiKey"
            placeholder="Enter your API key here to browse the data."/>
          <Button type="submit">View Legislation</Button>
        </form>
      </ApiKeyFormContainer>
    </PublicCard>
  )
}
