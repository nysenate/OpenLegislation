import React from 'react'
import styled from "styled-components"


/**
 * Common Input component.
 * Params:
 *    width: the minimum width for this input, defaults to 100px.
 */
const Input = styled.input`
  min-width: ${props => props.width ? props.width : "100px"};
  border: 0;
  border-bottom: .5px solid ${props => props.invalid ? props.theme.colors.red5 : props.theme.colors.gray3};
  font-size: ${props => props.theme.fontSizes.normal};
  font-weight: 400;
  :focus {
    border: 0;
    border-bottom: 2px solid ${props => props.invalid ? props.theme.colors.red5 : props.theme.colors.blue6}; 
    outline: none
  }
`

export default Input