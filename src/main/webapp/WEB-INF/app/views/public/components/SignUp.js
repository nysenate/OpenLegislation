import React from 'react'
import {
  Button,
  Input,
  PublicCard,
  SubTitle,
  Column,
  ErrorMessage,
  TitleSmall,
  Checkbox
} from "../style"
import apiSignup from "app/apis/apiSignup";
import {IconWarning} from "app/components/icons";

const breakingChangesSub = {
  title: 'Breaking Changes', enumVal: 'BREAKING_CHANGES', checked: true,
  desc: "Sign up for emails regarding breaking changes to the API."
}

const newFeaturesSub = {
  title: 'New Features', enumVal: 'NEW_FEATURES', checked: false,
  desc: "Sign up to hear about new features added to the API."
}

const initialState = {
  name: '',
  email: '',
  breakingChanges: true,
  newFeatures: false,
  loading: false,
  errorMsg: '',
  registeredMsg: '',
}

const registerReducer = function (state, action) {
  switch (action.type) {
    case 'input':
      return {
        ...state,
        [action.name]: action.value
      }
    case 'submit':
      return {
        ...state,
        loading: true,
        errorMsg: ''
      }
    case 'error':
      return {
        ...state,
        loading: false,
        errorMsg: action.value.message
      }
    case 'success':
      return {
        ...state,
        loading: false,
        registeredMsg: action.value.message
      }
    default:
      return alert("Invalid action type")
  }
}

export default function SignUp() {
  const [state, dispatch] = React.useReducer(registerReducer, initialState)

  const handleSubmit = e => {
    e.preventDefault();
    dispatch({type: 'submit'})

    let subs = [];
    if (state.breakingChanges) {
      subs.push(breakingChangesSub.enumVal)
    }
    if (state.newFeatures) {
      subs.push(newFeaturesSub.enumVal)
    }

    apiSignup(state.name, state.email, subs)
      .then((success) => {
        dispatch({
          type: 'success',
          value: success
        })
      })
      .catch((error) => {
        dispatch({
          type: 'error',
          value: error
        })
      })
  }

  return (
    <PublicCard>
      <header>
        <SubTitle>Sign up for an API Key</SubTitle>
      </header>
      <p>
        By signing up for a key you can access the API to power your own legislative apps.<br/>
        Simply provide a name and a valid email and we'll send over the credentials.
      </p>
      <form onSubmit={handleSubmit}>
        <Column>
          <div>
            <Input
              width="200px"
              type="text"
              name="name"
              placeholder="Name"
              onChange={(e) => dispatch({
                type: 'input',
                name: 'name',
                value: e.target.value
              })}
              value={state.name}
            />
            <Input
              width="200px"
              type="text"
              name="email"
              placeholder="Email"
              onChange={(e) => dispatch({
                type: 'input',
                name: 'email',
                value: e.target.value
              })}
              value={state.email}
            />
          </div>
          <div>
            <Checkbox
              type="checkbox"
              name="breakingChanges"
              onChange={(e) => dispatch({
                type: 'input',
                name: 'breakingChanges',
                value: e.target.checked
              })}
              checked={state.breakingChanges}
            />
            <label htmlFor="breakingChanges">{breakingChangesSub.desc}</label>
          </div>
          <div>
            <Checkbox
              type="checkbox"
              name="newFeatures"
              onChange={(e) => dispatch({
                type: 'input',
                name: 'newFeatures',
                value: e.target.checked
              })}
              checked={state.newFeatures}
            />
            <label htmlFor="newFeatures">{newFeaturesSub.desc}</label>
          </div>
        </Column>
        <Button type="submit">Get API Key</Button>
      </form>
      {state.errorMsg &&
      <ErrorMessage><IconWarning/>{state.errorMsg}</ErrorMessage>
      }
      {state.registeredMsg &&
      <TitleSmall>Thanks for signing up, please check your email to receive your API key.</TitleSmall>
      }
      {state.loading &&
      <TitleSmall>Your API key is being created, one sec.</TitleSmall>
      }
    </PublicCard>
  )
}