import React from 'react'
import ReactDOM from 'react-dom'
import './app.scss'
import Public from './public/Public'

function App() {
  return (
    <div>
      <Public />
    </div>
  )
}

ReactDOM.render(<App/>, document.getElementById('app'))
