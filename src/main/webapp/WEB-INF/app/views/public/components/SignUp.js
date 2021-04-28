import React from 'react'
import {
  Button,
  Input,
  PublicCard,
  SubTitle,
  Column
} from "../style"

export default function SignUp() {
  const name = React.useRef();
  const email = React.useRef();

  return (
    <PublicCard>
      <header>
        <SubTitle>Sign up for an API Key</SubTitle>
      </header>
      <p>
        By signing up for a key you can access the API to power your own legislative apps.
        Simply provide a name and a valid email and we'll send over the credentials.
      </p>
      <form onSubmit={() => alert("hello")}>
        <Column>
          <div>
            <Input
              width="200px"
              ref={name}
              type="text"
              name="name"
              placeholder="Name"/>
            <Input
              width="200px"
              ref={email}
              type="text"
              name="email"
              placeholder="Email"/>
          </div>
          <div>
            <Input
              type="checkbox"
              name="breakingChanges"
              defaultChecked/>
            <label htmlFor="breakingChanges">Sign up for emails regarding breaking changes to the API.</label>
          </div>
          <div>
            <Input
              type="checkbox"
              name="newFeatures"
              defaultUnChecked/>
            <label htmlFor="newFeatures">Sign up to hear about new features added to the API.</label>
          </div>
        </Column>
        <Button type="submit">Get API Key</Button>
      </form>
    </PublicCard>
  )
}