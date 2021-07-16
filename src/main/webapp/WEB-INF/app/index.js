import React from 'react'
import ReactDOM from 'react-dom'
import './icons.scss'
import './app.css'
import PublicView from './views/public'
import LegislationView from "app/views/legislation";

function App() {
  return (
    <>
      {/*<PublicView/>*/}

      <LegislationView />
    </>
  )
}

ReactDOM.render(<App />, document.getElementById('app'))
