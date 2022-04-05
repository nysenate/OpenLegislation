import React from "react"
import {
  Link,
  Route,
  Switch
} from "react-router-dom";
import PrivateRoute from "app/shared/PrivateRoute";


export default function AccountSettings({ setHeaderText }) {

  return (
    <>
      <Switch>
        <PrivateRoute path="/admin/account/password">
          <AccountPassword setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/account/notifications">
          <AccountNotifications setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/account/users">
          <ManageAdminUsers setHeaderText={setHeaderText} />
        </PrivateRoute>
        <Route>
          <AccountSettingsMenu setHeaderText={setHeaderText} />
        </Route>
      </Switch>
    </>
  )
}

function AccountSettingsMenu({ setHeaderText }) {

  React.useEffect(() => {
    setHeaderText("Admin Account Settings")
  }, [])

  return (
    <div className="m-3">
      <h3 className="h3">Manage Admin Account Settings</h3>
      <hr className="mb-6" />
      <ul>
        <li className="my-1"><Link to="/admin/account/password">Change Password</Link></li>
        <li className="my-1"><Link to="/admin/account/notifications">Configure Notifications</Link></li>
        <li className="my-1"><Link to="/admin/account/users">Configure Admin Accounts</Link></li>
      </ul>
    </div>
  )
}

function AccountPassword({ setHeaderText }) {
  return (
    <div>
      change password!
    </div>
  )
}

function AccountNotifications({ setHeaderText }) {
  return (
    <div>
      Account notifications
    </div>
  )
}

function ManageAdminUsers({ setHeaderText }) {
  return (
    <div>
      Manage Admin Users
    </div>
  )
}