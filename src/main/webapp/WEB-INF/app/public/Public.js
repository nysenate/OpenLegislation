import React from 'react'
import '../app.scss'

export default function Public () {
  return (
    <div className={'public-home-page'}>
      <section className={'hero-container-public'}>
        <h1>
          <img src="/static/img/nys_logo224x224.png"/>Open Legislation
        </h1>
      </section>
      <div className="pop-out-container">
        <div className="project-desc">
          Developed in-house at the <a href="http://www.nysenate.gov">New York State Senate</a>, Open Legislation is a
          public web service that provides access
          to NYS legislative data from the Legislative Bill Drafting Commission system through a JSON API.
        </div>
      </div>
    </div>
  )
}
