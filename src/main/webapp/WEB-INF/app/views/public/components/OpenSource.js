import React from 'react'
import {
  PublicCard,
  SubTitle,
} from "../style"

export default function OpenSource() {
  return (
    <PublicCard>
      <header>
        <SubTitle>Open Source</SubTitle>
      </header>
      <p>
        OpenLegislation is developed using several open-source packages and frameworks.
      </p>
      <address>
        <p>
          The source code <a href="http://www.github.com/nysenate/OpenLegislation">is published on GitHub</a>.
          Feel free to open any tickets with issues you are having or contact the development
          team at <a href="mailto:senatedev@nysenate.gov">senatedev@nysenate.gov</a>.
        </p>
      </address>
    </PublicCard>
  )
}