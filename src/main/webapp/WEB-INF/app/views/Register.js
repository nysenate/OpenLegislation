import React from "react"
import { useParams } from "react-router-dom";
import ErrorMessage from "../shared/ErrorMessage";


/**
 * This page confirms a users email address.
 * @constructor
 */
export default function Register() {
  const [ isRegistered, setIsRegistered ] = React.useState(false)
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const { token } = useParams()

  React.useEffect(() => {
    setErrorMsg("")
    registerToken(token)
      .then(() => setIsRegistered(true))
      .catch(err => setErrorMsg(err.message))
  }, [ token ])

  return (
    <div className="text-center my-10">
      {errorMsg &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      {isRegistered &&
        <h2 className="h2">Thank you for registering! Your API key has been emailed to you.</h2>
      }
    </div>
  )
}

function registerToken(token) {
  const url = `/register/token/${token}`
  return fetchUrl(url)
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}
