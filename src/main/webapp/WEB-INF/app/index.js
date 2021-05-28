import React from 'react'
import ReactDOM from 'react-dom'
import GlobalStyle from './GlobalStyles'
import './app.scss'
import './icons.scss'
import PublicView from './views/public'
import {ThemeProvider} from 'styled-components';
import theme from './Theme';

function App() {
  return (
    <div>
      <ThemeProvider theme={theme}>
        <GlobalStyle/>
        <PublicView/>
      </ThemeProvider>
    </div>
  )
}

ReactDOM.render(<App/>, document.getElementById('app'))
