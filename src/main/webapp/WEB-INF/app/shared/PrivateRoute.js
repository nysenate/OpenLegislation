import React from 'react'
import {
  Redirect,
  Route
} from "react-router-dom";
import useIsPermitted from "app/shared/useIsPermitted";


export default function PrivateRoute({ permissionName, children, ...rest }) {
  const isPermitted = useIsPermitted(permissionName)

  if (isPermitted === undefined) {
    return null
  }

  if (isPermitted === false) {
    return <Redirect to="/" />
  }

  if (isPermitted === true) {
    return (
      <Route {...rest} render={() => {
        return children
      }}
      />
    )
  }
}


