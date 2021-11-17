import React from "react";
import { hasPermission } from "app/apis/authApi";


/**
 * Contains the common code for checking if the current user has a given permission.
 * @param permissionName
 * @returns undefined | true | false
 *          undefined - if the request is pending.
 *          true - if the user has the given permission.
 *          false - if the user does not have the given permission.
 */
export default function useIsPermitted(permissionName) {
  const [ isPermitted, setIsPermitted ] = React.useState(undefined)

  React.useEffect(() => {
    hasPermission(permissionName)
      .then((isPermitted) => setIsPermitted(isPermitted))
      .catch((err) => {
        console.error(err)
        setIsPermitted(false)
      })
  }, [permissionName])

  return isPermitted
}