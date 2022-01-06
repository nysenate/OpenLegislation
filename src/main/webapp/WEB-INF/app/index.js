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
import { AuthProvider } from "./shared/useAuth";


function App() {
  return (
    <AuthProvider>
      <Router>
        <Switch>
          <Route exact path="/">
            <PublicView />
          </Route>
          <Route>
            <Home />
          </Route>
        </Switch>
      </Router>
    </AuthProvider>
  )
}

ReactDOM.render(<App />, document.getElementById('app'))
