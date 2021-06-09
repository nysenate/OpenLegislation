import styled from 'styled-components'

export const Card = styled.section`
  padding: ${props => props.theme.space.normal};
  margin-top: ${props => props.theme.space.large};
  color: ${props => props.theme.colors.gray9};
  background: #fff;
  box-shadow: 0 1px 3px ${props => props.theme.colors.gray3};
`
