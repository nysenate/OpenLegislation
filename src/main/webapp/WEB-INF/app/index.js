import React from 'react'
import ReactDOM from 'react-dom'
import {
  BrowserRouter as Router,
} from "react-router-dom";
import './icons.scss'
import './app.css'
import PublicView from './views/public'
import Home from "app/views/home";

function App() {
  return (
    <React.Fragment>
      <Router>
        {/*<PublicView/>*/}
        <Home />
      </Router>
    </React.Fragment>
  )
}

ReactDOM.render(<App />, document.getElementById('app'))
