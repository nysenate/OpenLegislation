import React from 'react'
import ReactDOM from 'react-dom'
import {
  BrowserRouter as Router,
  Redirect,
  Route,
  Switch,
} from "react-router-dom";
import './app.css'
import PublicView from './views/public/PublicView'
import Home from "app/views/home/Home";
import useAuth, { AuthProvider } from "./shared/useAuth";
import useGlobals, { GlobalsProvider } from "app/shared/useGlobals";
import Logout from "app/views/logout/Logout";
import ErrorBoundary from "app/views/ErrorBoundary";
import Register from "app/views/Register";


function App() {
  return (
    <ErrorBoundary>
      <GlobalsProvider>
        <RequireGlobals>
          <Router>
            <AuthProvider>
              <AppRouter />
            </AuthProvider>
          </Router>
        </RequireGlobals>
      </GlobalsProvider>
    </ErrorBoundary>
  )
}

/**
 * Adds redirects at the home page route so that authorized/authenticated users are directed to the bills page
 * and non-authenticated users are directed to the public page.
 * @returns {JSX.Element}
 * @constructor
 */
function AppRouter() {
  const auth = useAuth()
  const globals = useGlobals()
  return (
    <Switch>
      <Route exact path="/">
        {(auth.isAuthed() === true || globals.isWhitelisted)
          ? <Redirect to="/bills" />
          : <Redirect to="/public" />
        }
      </Route>
      <Route exact path="/public">
        <PublicView />
      </Route>
      <Route exact path="/logout">
        <Logout />
      </Route>
      <Route exact path="/register/:token">
        <Register />
      </Route>
      <Route>
        <Home />
      </Route>
    </Switch>
  )
}

/**
 * Prevents the rendering of our application until globals have been loaded.
 */
function RequireGlobals({ children }) {
  const globals = useGlobals()

  if (!globals) {
    return null
  }
  return (
    <React.Fragment>
      {children}
    </React.Fragment>
  )
}

ReactDOM.render(<App />, document.getElementById('app'))
