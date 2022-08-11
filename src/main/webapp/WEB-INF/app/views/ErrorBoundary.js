import React from 'react'

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-page text-center pt-10">
          <p className="text-xl text-white mb-6">Uh oh, Something went wrong...</p>
          <a href="/" className="text-white border-white border-solid border-b-1 cursor-pointer">Return home</a>
        </div>
      )
    }

    return this.props.children
  }
}