import React from 'react'
import styled from "styled-components";
import Card from "../common/Card";

const StyledAboutCard = styled(Card)`
  position: relative;
  max-width: 96rem;
  padding: 5rem 3rem;
  margin: -10rem auto 0 auto;
  font-weight: 400;
  font-size: 1.9rem;
  text-align: center;
  
  & > a {
    color: #008cba;
  }
`

export default function AboutCard() {
  return (
    <StyledAboutCard>
      Developed in-house at the <a href="http://www.nysenate.gov">New York State Senate</a>,
      Open Legislation is a public web service that provides access to NYS legislative data
      from the Legislative Bill Drafting Commission system through a JSON API.
    </StyledAboutCard>
  )
}
