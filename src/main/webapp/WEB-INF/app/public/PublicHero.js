import React from 'react'
import styled from "styled-components";

const Hero = styled.section`
  margin-bottom: 0;
  height: 30rem;
  background: #008cba;
  position: relative;
   
  h1 {
    margin: 0;
    font-family: 'Roboto Slab', sans-serif;
    font-weight: 400;
    color: #fff;
    line-height: 20rem;
    font-size: 8rem;
    text-align: center;
    text-shadow: 0 0 0.1rem #235674;
  }
    
  img {
    height: 12rem;
    vertical-align: text-top;
    margin-right: 2rem;
  }
`

export default function PublicHero() {

  return (
    <Hero>
      <h1>
        <img src="/static/img/nys_logo224x224.png"/>Open Legislation
      </h1>
    </Hero>
  )
}