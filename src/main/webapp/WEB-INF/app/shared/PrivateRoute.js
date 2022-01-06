import React from 'react'
import {
  Redirect,
  Route
} from "react-router-dom";
import useAuth from "app/shared/useAuth";


export default function PrivateRoute({ children, ...rest }) {
  const auth = useAuth()

  if (rest.path?.startsWith("/admin")) {
    return (
      <Route {...rest} render={() => {
        return auth.isAdmin === true
          ? children
          : <Redirect to="/admin" />
      }} />
    )
  }
  else {
    return (
      <Route {...rest} render={() => {
        return auth.isAuthed === true
          ? children
          : <Redirect to="/" />
      }} />
    )
  }
}
