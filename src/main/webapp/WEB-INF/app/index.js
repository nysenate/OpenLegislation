import React from 'react'
import ReactDOM from 'react-dom'
import GlobalStyle from './GlobalStyles'
import './app.scss'
import PublicView from './views/public'

function App() {
  return (
    <div>
      <GlobalStyle />
      <PublicView />
    </div>
  )
}

ReactDOM.render(<App/>, document.getElementById('app'))
