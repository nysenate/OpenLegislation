import React from 'react'

export default function OpenSource() {
  return (
    <section className="card mt-12 text-center">
      <header>
        <h2 className="h3 mb-3">Open Source</h2>
      </header>
      <p className="mb-2 text text-left md:text-center">
        OpenLegislation is developed using several open-source packages and frameworks.
      </p>
      <address className="not-italic">
        <p className="text text-left md:text-center">
          The source code <a href="http://www.github.com/nysenate/OpenLegislation" className="link">is published on GitHub</a>.
          Feel free to open any tickets with issues you are having or contact the development
          team at <a href="mailto:senatedev@nysenate.gov" className="link">senatedev@nysenate.gov</a>.
        </p>
      </address>
    </section>
  )
}
