import React from 'react'
import ReactDOM from 'react-dom'
import './icons.scss'
import './app.css'
import PublicView from './views/public'

function App() {
  return (
    <>
      <PublicView/>
    </>
  )
}

ReactDOM.render(<App/>, document.getElementById('app'))
