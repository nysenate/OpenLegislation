import React from 'react'
import styled from "styled-components";
import Title from './Title'
import SenateSealLogo from "./SenateSealLogo";

export default function Header() {
  return (
    <header>
      <Background>
        <Title>
          <SenateSealLogo />Open Legislation
        </Title>
      </Background>
    </header>
  )
}

const Background = styled.section`
  margin-bottom: 0;
  height: 30rem;
  background: #008cba;
  position: relative;
`
