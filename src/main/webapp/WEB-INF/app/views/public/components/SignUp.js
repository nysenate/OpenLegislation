import React from 'react'
import apiSignup from "app/apis/apiSignup";
import { Warning } from "phosphor-react";
import ErrorMessage from "app/shared/ErrorMessage";

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
  const [ state, dispatch ] = React.useReducer(registerReducer, initialState)

  const handleSubmit = e => {
    e.preventDefault();
    dispatch({ type: 'submit' })

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
    <section className="card mt-12 text-center">
      <header>
        <h2 className="h3 mb-3">Sign up for an API Key</h2>
      </header>
      <p className="mb-2 text text-left md:text-center">
        By signing up for a key you can access the API to power your own legislative apps.
      </p>
      <p className="mb-2 text text-left md:text-center">
        Simply provide a name and a valid email and we'll send over the credentials.
      </p>
      <form onSubmit={handleSubmit}>
        <div className="flex flex-col">
          <div className="">
            <input
              className="input text-center m-2"
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
            <input
              className="input text-center m-2"
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
          <div className="mt-3 text-left md:text-center">
            <input
              className="mr-2"
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
          <div className="mt-3 text-left md:text-center">
            <input
              className="mr-2"
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
        </div>
        <button className="btn btn--primary my-3 w-36" type="submit">Get API Key</button>
      </form>
      {state.errorMsg &&
        <ErrorMessage>
           {state.errorMsg}
        </ErrorMessage>
      }
      {state.registeredMsg &&
        <h3 className="h5">Thanks for signing up, please check your email to receive your API key.</h3>
      }
      {state.loading &&
        <h3 className="h5">Your API key is being created, one sec.</h3>
      }
    </section>
  )
}