import React from 'react'

export default function Documentation() {
  return (
    <section className="card mt-12 text-center">
      <header>
        <h2 className="h3 mb-3">Documentation</h2>
      </header>
      <p className="text text-left md:text-center">
        See our <a target="_blank" href="/docs">documentation</a> for details about our API.
      </p>
      <iframe className="h-screen mt-6 hidden lg:block" width="100%" src="/static/docs/html/index.html"/>
    </section>
  )
}
