import React from "react";
import {
  getEmail,
  updateEmail
} from "app/apis/subscriptionApi";
import ErrorMessage from "../../shared/ErrorMessage";


export default function Email({ apiKey }) {
  const [ email, setEmail ] = React.useState("");
  const [ message, setMessage ] = React.useState("")
  const [ errorMsg, setErrorMsg ] = React.useState("")

  React.useEffect(() => {
    getEmail(apiKey)
      .then((data) => setEmail(data.message))
  }, [ apiKey ]);

  const onSubmit = (e) => {
    e.preventDefault()
    setMessage("")
    setErrorMsg("")
    updateEmail(apiKey, email)
      .then(() => setMessage("Email updated!"))
      .catch((error) => setErrorMsg(error.message))
  }

  return (
    <form className="flex justify-center text--large my-3" onSubmit={onSubmit}>
      <div>
        <div className="h5 mb-3">Update the email used for notifications.</div>
        <input value={email}
               type="email"
               onChange={(e) => setEmail(e.target.value)}
               className="input block w-72" />
        <button className="btn btn--primary my-3 w-55" type="submit">Update email</button>
        <div>{errorMsg ? <ErrorMessage>{errorMsg}</ErrorMessage> : message}</div>
      </div>
    </form>
  )
}
