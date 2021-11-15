import React from 'react'
import {
  Redirect,
  Route
} from "react-router-dom";
import { hasPermission } from "app/apis/authApi";


export default function PrivateRoute({ permissionName, children, ...rest }) {
  const [ isPermitted, setIsPermitted ] = React.useState(undefined)

  React.useEffect(() => {
    hasPermission(permissionName)
      .then((isPermitted) => setIsPermitted(isPermitted))
      .catch((err) => {
        console.error(err)
        setIsPermitted(false)
      })
  }, [permissionName])

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
