import React from 'react'
import {
  DocsIframe,
  PublicCard,
  SubTitle,
} from "../style"

export default function Documentation() {
  return (
    <PublicCard>
      <header>
        <SubTitle>How to use the API</SubTitle>
      </header>
      <a target="_blank" href="/docs">View docs in new window</a>
      <DocsIframe width="100%" src="/static/docs/html/index.html"/>
    </PublicCard>
  )
}