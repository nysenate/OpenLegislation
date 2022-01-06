import React from 'react'
import AdminLogin from "app/views/admin/AdminLogin";
import {
  Route,
  Switch
} from "react-router-dom";
import Configuration from "app/views/admin/configuration/Configuration";
import PrivateRoute from "app/shared/PrivateRoute";

export default function Admin() {

  return (
    <Switch>
      <PrivateRoute path="/admin/config">
        <Configuration />
      </PrivateRoute>
      <Route path="/admin">
        <AdminLogin />
      </Route>
    </Switch>
  )
}