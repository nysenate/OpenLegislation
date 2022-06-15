import React from "react";
import {
  emailInUse,
  getEmail,
  updateEmail
} from "app/apis/subscriptionApi";
import Input from "app/shared/Input";

export default function Email({ apiKey }) {
  const [ email, setEmail ] = React.useState("");
  const [ message, setMessage ] = React.useState("")
  React.useEffect(() => {
    getEmail(apiKey).then((data) => setEmail(data.message))
  }, [ apiKey ]);

  const onSubmit = (e) => {
    e.preventDefault()
    emailInUse(email).then((res) => {
      if (res[0]) {
        setMessage("Email is already in use.")
      } else {
        updateEmail(apiKey, email)
        setMessage("Email updated!")
      }
    })
  }
  return (
    <form className="flex justify-center text--large" onSubmit={onSubmit}>
      <div>
        <Input label="Email" value={email} onChange={(e) => setEmail(e.target.value)}
               name="emailField" className="w-full" />
        <button className="btn btn--primary my-3 w-55" type="submit">Update email</button>
        <div>{message}</div>
      </div>
    </form>
  )
}
