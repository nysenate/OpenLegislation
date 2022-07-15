import React from 'react'
import AdminLogin from "app/views/admin/AdminLogin";
import {
  Link,
  Redirect,
  Route,
  Switch
} from "react-router-dom";
import Configuration from "app/views/admin/configuration/Configuration";
import PrivateRoute from "app/shared/PrivateRoute";
import ContentContainer from "app/shared/ContentContainer";
import Caches from "app/views/admin/caches/Caches";
import Indices from "app/views/admin/indices/Indices";
import Reports from "app/views/admin/reports/Reports"
import ApiMonitor from "app/views/admin/logs/ApiMonitor";
import DataProcessLog from "app/views/admin/logs/DataProcessLog";
import ApiLogSearch from "app/views/admin/logs/ApiLogSearch";
import ChangePassword from "app/views/admin/accounts/ChangePassword";
import ManageAdminUsers from "app/views/admin/accounts/ManageAdminUsers";
import ManageNotifications from "app/views/admin/accounts/ManageNotifications";
import BatchEmail from "app/views/admin/email/BatchEmail";
import useAuth from "app/shared/useAuth";
import SpotcheckReports from "app/views/admin/spotchecks/SpotcheckReports";


export default function Admin({ setHeaderText }) {
  const auth = useAuth()

  return (
    <ContentContainer>
      <Switch>
        <PrivateRoute path="/admin/config">
          <Configuration setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/caches">
          <Caches setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/indices">
          <Indices setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/reports">
          <Reports setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/logs/monitor">
          <ApiMonitor setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/logs/dataprocess">
          <DataProcessLog setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/logs/search">
          <ApiLogSearch setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/account/password">
          <ChangePassword setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/account/notifications">
          <ManageNotifications setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/account/users">
          <ManageAdminUsers setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/email">
          <BatchEmail setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/spotchecks">
          <SpotcheckReports setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/index">
          <AdminDashboard setHeaderText={setHeaderText} />
        </PrivateRoute>
        <Route path="/admin/login">
          <AdminLogin setHeaderText={setHeaderText} />
        </Route>
        <Route path="/admin">
          {auth.isAuthed()
            ? <Redirect to="/admin/index" />
            : <Redirect to="/admin/login" />
          }
        </Route>
      </Switch>
    </ContentContainer>
  )
}

function AdminDashboard({ setHeaderText }) {
  React.useEffect(() => {
    setHeaderText("Admin Dashboard")
  }, [])
  return (
    <div className="m-3 mt-6">
      <div className="grid grid-cols-2 gap-y-10 gap-x-20">
        <div>
          <h3 className="h4">Configuration</h3>
          <hr className="mb-3" />
          <ul className="list">
            <li><Link to="/admin/config" className="link">App Properties</Link></li>
            <li><Link to="/admin/caches" className="link">In Memory Caches</Link></li>
            <li><Link to="/admin/indices" className="link">Elastic Search Indexes</Link></li>
          </ul>
        </div>

        <div>
          <h3 className="h4">Manage Admin Account Settings</h3>
          <hr className="mb-3" />
          <ul className="list">
            <li><Link to="/admin/account/password" className="link">Change Password</Link></li>
            <li><Link to="/admin/account/notifications" className="link">Configure Notifications</Link></li>
            <li><Link to="/admin/account/users" className="link">Configure Admin Accounts</Link></li>
          </ul>
        </div>

        <div>
          <h3 className="h4">Logs</h3>
          <hr className="mb-3" />
          <ul className="list">
            <li><Link to="/admin/logs/monitor" className="link">API Monitor</Link></li>
            <li><Link to="/admin/logs/search" className="link">API Log Search</Link></li>
            <li><Link to="/admin/logs/dataprocess" className="link">Data Process Logs</Link></li>
          </ul>
        </div>

        <div>
          <h3 className="h4">Spotchecks</h3>
          <hr className="mb-3" />
          <ul className="list">
            <li><Link to="/admin/reports" className="link">Run Reports</Link></li>
            <li><Link to="/admin/spotchecks" className="link">View Spotchecks</Link></li>
          </ul>
        </div>

        <div>
          <h3 className="h4">API Users</h3>
          <hr className="mb-3" />
          <ul className="list">
            <li><Link to="/admin/email" className="link">Send Batch Email</Link></li>
          </ul>
        </div>
      </div>
    </div>
  )
}