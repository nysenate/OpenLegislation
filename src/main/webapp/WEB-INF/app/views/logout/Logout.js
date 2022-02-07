import React from "react"
import { Redirect } from "react-router-dom";
import useAuth from "app/shared/useAuth";


/**
 * Logout the user and redirect to the public page.
 */
export default function Logout() {
  const auth = useAuth()

  React.useEffect(() => {
    auth.logoutUser()
  }, [])

  return (
    <Redirect to="/" />
  )
}
