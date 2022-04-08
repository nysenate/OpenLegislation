import React from "react"
import Input from "app/shared/Input";
import { useHistory } from "react-router-dom";
import useAuth from "app/shared/useAuth";
import { Warning } from "phosphor-react";


export default function AdminLogin({ setHeaderText }) {
  const [ username, setUsername ] = React.useState("")
  const [ password, setPassword ] = React.useState("")
  const [ error, setError ] = React.useState("")
  const history = useHistory()
  const auth = useAuth()

  React.useEffect(() => {
    setHeaderText("Admin Login")
  }, [])

  const onSubmit = (e) => {
    e.preventDefault()
    auth.loginAdminUser(username, password)
      .then(() => history.push("/admin/index"))
      .catch((err) => {
        if (err.errorCode === 401) {
          setError("Invalid credentials.")
        } else {
          setError("Error during authentication.")
        }
      })
  }

  return (
    <div className="p-3">
      <form onSubmit={onSubmit}>
        <Input label="Username"
               value={username}
               onChange={(e) => setUsername(e.target.value)}
               name="username" />
        <Input label="Password"
               value={password}
               onChange={(e) => setPassword(e.target.value)}
               name="password"
               type="password" />
        <button className="btn btn--primary my-3 w-36" type="submit">Login</button>
      </form>
      {error &&
        <div>
          <p className="flex items-center gap-x-1 text text--error">
            <Warning size="1.2rem" /> <span>{error}</span>
          </p>
        </div>
      }
    </div>
  )
}
