import React from 'react'

/**
 * 404 page
 */
export default function NotFound() {

  React.useEffect(() => {
    document.title = "Page Does Not Exist"
  }, [])

  return (
    <div className="error-page text-center pt-10">
      <h4 className="text-9xl text-white mb-6">404</h4>
      <p className="text-2xl">Sorry, the page you requested appears to be missing</p>
    </div>
  )
}