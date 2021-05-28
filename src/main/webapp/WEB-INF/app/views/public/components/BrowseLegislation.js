import React from 'react'
import {
  ApiKeyFormContainer,
  Input,
  Button,
  PublicCard,
  SubTitle,
  ErrorMessage
} from "../style"
import {IconWarning} from "app/components/icons"
import {apiKeyLogin} from "app/apis/apiKeyLogin"

export default function BrowseLegislation() {
  const apiKeyRef = React.useRef()
  const [error, setError] = React.useState(false)

  const handleSubmit = e => {
    e.preventDefault()

    const apiKey = apiKeyRef.current.value.trim();
    apiKeyLogin(apiKey)
      .then((result) => {
        setError(false)
      })
      .catch((error) => {
        setError(true)
      });
  }

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
        <form onSubmit={handleSubmit}>
          <Input
            width="500px"
            ref={apiKeyRef}
            type="text"
            name="apiKey"
            placeholder="Enter your API key here to browse the data."/>
          <Button type="submit">View Legislation</Button>
        </form>
      </ApiKeyFormContainer>
      {error === true &&
        <div>
          <ErrorMessage><IconWarning/>Please enter a valid api key, or sign up for one below.</ErrorMessage>
        </div>
      }
    </PublicCard>
  )
}
