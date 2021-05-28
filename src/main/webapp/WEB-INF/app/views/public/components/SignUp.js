import React from 'react'
import {
  Button,
  Input,
  PublicCard,
  SubTitle,
  Column
} from "../style"
import apiSignup from "app/apis/apiSignup";

const breakingChangesSub =  {
  title: 'Breaking Changes', enumVal: 'BREAKING_CHANGES', checked: true,
  desc: "Sign up for emails regarding breaking changes to the API."
}

const newFeaturesSub =  {
  title: 'New Features', enumVal: 'NEW_FEATURES', checked: false,
  desc: "Sign up to hear about new features added to the API."
}

export default function SignUp() {
  const [signedUp, setSignedUp] = React.useState(false)
  const [error, setError] = React.useState(false)
  const name = React.useRef()
  const email = React.useRef()
  const breakingChangesRef = React.useRef(breakingChangesSub)
  const newFeaturesRef = React.useRef(newFeaturesSub)

  const handleSubmit = e => {
    e.preventDefault();

    // TODO validate email/name

    let subs = [];
    if (breakingChangesRef.current.checked) {
      subs.push(breakingChangesSub.enumVal)
    }
    if (newFeaturesRef.current.checked) {
      subs.push(newFeaturesSub.enumVal)
    }

    apiSignup(name.current.value, email.current.value, subs)
      .then((success) => {
        setError(false)
        setSignedUp(true)
      })
      .catch((error) => {
        setError(true)
        console.warn(error);
      })

  }

  return (
    <PublicCard>
      <header>
        <SubTitle>Sign up for an API Key</SubTitle>
      </header>
      <p>
        By signing up for a key you can access the API to power your own legislative apps.
        Simply provide a name and a valid email and we'll send over the credentials.
      </p>
      <form onSubmit={handleSubmit}>
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
              ref={breakingChangesRef}
              name="breakingChanges"
              defaultChecked={breakingChangesSub.checked}/>
            <label htmlFor="breakingChanges">{breakingChangesSub.desc}</label>
          </div>
          <div>
            <Input
              type="checkbox"
              ref={newFeaturesRef}
              name="newFeatures"
              defaultChecked={newFeaturesSub.checked}/>
            <label htmlFor="newFeatures">{newFeaturesSub.desc}</label>
          </div>
        </Column>
        <Button type="submit">Get API Key</Button>
      </form>
    </PublicCard>
  )
}