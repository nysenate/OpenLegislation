import React from 'react'
import { useHistory } from "react-router-dom";
import useAuth from "../../../shared/useAuth";
import { Warning } from "phosphor-react";

export default function BrowseLegislation() {
  const apiKeyRef = React.useRef()
  const [ error, setError ] = React.useState(false)
  const history = useHistory()
  const auth = useAuth()

  const handleSubmit = e => {
    e.preventDefault()

    const apiKey = apiKeyRef.current.value.trim();
    auth.loginApiUser(apiKey)
      .then(() => {
        setError(false)
        history.push("/bills/search")
      })
      .catch((err) => setError(true))
  }

  return (
    <section className="card mt-12 text-center">
      <header>
        <h2 className="h3 mb-3">
          Browse Legislation
        </h2>
      </header>
      <div className="mb-4 text text-left md:text-center">
        <p className="mb-2">
          We have an Open Legislation interface that we use to assist in our development.
        </p>
        <p className="text text-left md:text-center">
          Feel free to access it by using your API Key.
        </p>
      </div>

      <div className="text-center">
        <form onSubmit={handleSubmit}>
          <input
            ref={apiKeyRef}
            type="text"
            name="apiKey"
            placeholder="API key"
            className="input text-center w-40 sm:w-60 lg:w-80"
          />
          <button type="submit" className="btn btn--primary ml-3">View Legislation</button>
        </form>
      </div>
      {error &&
        <div className="mt-3">
          <p className="text text--error flex items-center justify-center gap-x-1">
            <Warning size="1.2rem" />
            Please enter a valid api key, or sign up for one below.
          </p>
        </div>
      }
    </section>
  )
}
