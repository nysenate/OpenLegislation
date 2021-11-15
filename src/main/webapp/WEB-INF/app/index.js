import React from 'react'
import ReactDOM from 'react-dom'
import {
  BrowserRouter as Router,
  Route,
  Switch,
} from "react-router-dom";
import './app.css'
import PublicView from './views/public/PublicView'
import Home from "app/views/home/Home";
import PrivateRoute from "app/shared/PrivateRoute";


function App() {
  return (
    <React.Fragment>
      <Router>
        <Switch>
          <Route exact path="/">
            <PublicView />
          </Route>
          <PrivateRoute permissionName="ui:view">
            <Home />
          </PrivateRoute>
        </Switch>
      </Router>
    </React.Fragment>
  )
}

ReactDOM.render(<App />, document.getElementById('app'))
