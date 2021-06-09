import React from 'react'
import styled from "styled-components"


export default function Title({ text, fontSize, color}) {

  return (
    <StyledTitle
      fontSize={fontSize}
      color={color}>
      {text}
    </StyledTitle>
  )
}

const StyledTitle = styled.h1`
  color: ${props => props.color ? props.color : props.theme.colors.gray9};
  font-size: ${props => props.fontSize ? props.fontSize : props.theme.fontSize.large};
  font-weight: normal;
`
