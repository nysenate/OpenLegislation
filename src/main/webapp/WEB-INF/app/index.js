import React from 'react'
import ReactDOM from 'react-dom'
import './app.scss'
import PublicView from './views/public'

function App() {
  return (
    <div>
      <PublicView />
    </div>
  )
}

ReactDOM.render(<App/>, document.getElementById('app'))
