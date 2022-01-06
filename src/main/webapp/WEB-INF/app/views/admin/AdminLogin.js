import React from "react"
import Input from "app/shared/Input";
import { loginAdmin } from "app/apis/authApi";
import { useHistory } from "react-router-dom";
import useAuth from "app/shared/useAuth";


export default function AdminLogin() {
  const [ username, setUsername ] = React.useState("")
  const [ password, setPassword ] = React.useState("")
  const history = useHistory()
  const auth = useAuth()

  const onSubmit = (e) => {
    e.preventDefault()
    auth.loginAdminUser(username, password)
      .then(() => history.push("/admin/config"))
  }

  return (
    <div>
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
        <button className="btn my-3 w-36" type="submit">Login</button>
      </form>
    </div>
  )
}
