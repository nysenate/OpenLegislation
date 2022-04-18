import React from 'react'
import { Link } from "react-router-dom";

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
        <div className="p-3">
          <p>Something went wrong...</p>
          <a href="/">Return home</a>
        </div>
      )
    }

    return this.props.children
  }
}