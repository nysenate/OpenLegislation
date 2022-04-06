import React from 'react'
import AdminLogin from "app/views/admin/AdminLogin";
import {
  Route,
  Switch
} from "react-router-dom";
import Configuration from "app/views/admin/configuration/Configuration";
import PrivateRoute from "app/shared/PrivateRoute";
import ContentContainer from "app/shared/ContentContainer";
import Caches from "app/views/admin/caches/Caches";
import Indices from "app/views/admin/indices/Indices";
import ApiMonitor from "app/views/admin/logs/ApiMonitor";
import DataProcessLog from "app/views/admin/logs/DataProcessLog";
import ApiLogSearch from "app/views/admin/logs/ApiLogSearch";

export default function Admin({ setHeaderText }) {

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
        <PrivateRoute path="/admin/logs/monitor">
          <ApiMonitor setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/logs/dataprocess">
          <DataProcessLog setHeaderText={setHeaderText} />
        </PrivateRoute>
        <PrivateRoute path="/admin/logs/search">
          <ApiLogSearch setHeaderText={setHeaderText} />
        </PrivateRoute>
        <Route path="/admin">
          <AdminLogin setHeaderText={setHeaderText} />
        </Route>
      </Switch>
    </ContentContainer>
  )
}