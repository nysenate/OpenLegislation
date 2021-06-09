import React from "react";
import { StyledCard } from "./style"

export default function Card({ className, children }) {
  return (
    <StyledCard className={className}>
      {children}
    </StyledCard>
  )
}
