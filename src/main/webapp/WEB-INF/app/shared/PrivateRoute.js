import React from 'react'
import {
  Redirect,
  Route
} from "react-router-dom";
import useAuth from "app/shared/useAuth";
import useGlobals from "app/shared/useGlobals";


/**
 * The PrivateRoute prevents navigation to a page the user is not authorized to view.
 *
 * - All admin pages (start with "/admin") require the user to be a logged in admin.
 * - Legislative data pages require login with an API key or browsing from whitelisted ip address.
 */
export default function PrivateRoute({ children, ...rest }) {
  const auth = useAuth()
  const globals = useGlobals()

  // Only authenticated admins can visit pages starting with "/admin"
  if (rest.path?.startsWith("/admin")) {
    return (
      <Route {...rest} render={() => {
        return auth.isAdmin() === true
          ? children
          : <Redirect to="/admin" />
      }} />
    )
  }
  // For all other pages, either the user must authenticate with an API key
  // or be on a whitelisted ip address.
  else {
    return (
      <Route {...rest} render={() => {
        return (auth.isAuthed() === true || globals.isWhitelisted)
          ? children
          : <Redirect to="/" />
      }} />
    )
  }
}
