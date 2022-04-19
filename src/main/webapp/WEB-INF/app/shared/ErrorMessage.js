import React from 'react'
import { Warning } from "phosphor-react";

export default function ErrorMessage({ children }) {
  return (
    <span className="text text--error inline-flex items-center gap-x-1">
      <Warning size="1.2rem" />
      <span>{children}</span>
    </span>
  )
}