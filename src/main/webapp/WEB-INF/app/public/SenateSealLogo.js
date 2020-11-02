import React from 'react'
import styled from 'styled-components'

const StyledSenateSealLogo = styled.img`
    height: 12rem;
    vertical-align: text-top;
    margin-right: 2rem;
`

export default function SenateSealLogo() {
  return (
    <StyledSenateSealLogo src="/static/img/nys_logo224x224.png" />
  )
}
