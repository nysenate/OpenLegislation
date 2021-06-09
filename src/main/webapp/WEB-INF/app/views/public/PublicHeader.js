import React from 'react'
import {useContext} from 'react'
import styled from 'styled-components'
import {ThemeContext} from 'styled-components'
import {SenateSealLogo} from 'app/views/public/style'
import Title from 'app/shared/Title'

export default function PublicHeader() {

  const themeContext = useContext(ThemeContext)

  return (
    <HeaderBackground>
      <HeaderContainer>
        <SenateSealLogo src="/static/img/nys_logo224x224.png"/>
        <Title
          fontSize={themeContext.fontSizes.huge}
          color={themeContext.colors.white}
          text='Open Legislation' />
      </HeaderContainer>
    </HeaderBackground>
  )
}

const HeaderBackground = styled.div`
  margin-bottom: 0;
  height: 300px;
  background: ${props => props.theme.colors.blue5};
  position: relative;
`

const HeaderContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: ${({ theme }) => theme.space.xlarge};
  > h1 {
    display: inline-block;
  }
`
