import React, {useContext} from 'react'
import styled from 'styled-components'
import {ThemeContext} from "styled-components"
import {
  Button,
  ErrorMessage
} from "../style"
import {IconWarning} from "app/components/icons"
import {apiKeyLogin} from "app/apis/apiKeyLogin"
import {Card} from "app/shared/Card"
import Title from "app/shared/Title"
import Input from "app/shared/Input";

export default function BrowseLegislation() {
  const apiKeyRef = React.useRef()
  const [error, setError] = React.useState(false)
  const themeContext = useContext(ThemeContext)

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
    <Card>
      <CenteredText>
        <header>
          <Title
            fontSize={themeContext.fontSizes.large}
            text='Browse Legislation'/>
        </header>
        <p>
          We have an Open Legislation interface that we use to assist in our development.
        </p>
        <p>
          Feel free to access it by using your API Key.
        </p>

        <ApiLoginFormContainer>
          <form onSubmit={handleSubmit}>
            <Input
              width="500px"
              ref={apiKeyRef}
              type="text"
              name="apiKey"
              placeholder="Enter your API key here to browse the data."/>
            <Button type="submit">View Legislation</Button>
          </form>
        </ApiLoginFormContainer>
        {error === true &&
        <div>
          <ErrorMessage><IconWarning/>Please enter a valid api key, or sign up for one below.</ErrorMessage>
        </div>
        }
      </CenteredText>
    </Card>
  )
}

const CenteredText = styled.div`
  text-align: center;
`

const ApiLoginFormContainer = styled.div`
  margin-top: 32px;
  input {
    text-align: center;
  }
`
