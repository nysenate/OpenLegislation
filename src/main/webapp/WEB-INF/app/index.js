import React from 'react'
import ReactDOM from 'react-dom'
import './index.css'

class App extends React.Component {

  componentDidMount() {
    fetch('/api/3/bills/updates')
        .then((res) => res.json())
        .then((res) => console.log(res))
  }

  render() {
    return (
      <div>
        Hello World!!
      </div>
    )
  }
}

ReactDOM.render(<App />, document.getElementById('app'))
