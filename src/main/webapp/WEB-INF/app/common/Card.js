import React from 'react'
import styled from 'styled-components'

const StyledCard = styled.section`
  background: #fff;
  box-shadow: 0 0.1rem 0.3rem #bbb;
`

export default function Card({ className, children }) {
  return (
    <StyledCard className={className}>
      {children}
    </StyledCard>
  )
}