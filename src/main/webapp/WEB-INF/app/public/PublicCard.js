import React from 'react'
import styled from "styled-components";
import Card from "../common/Card";

const PublicCard = styled(Card)`
  margin: 5rem auto 0 auto;
  padding: 2rem 2rem;
  max-width: 96rem;
  font-size: 1.9rem;
  font-weight: 300;
  text-align: center;
  
  & > p {
    margin: 1rem 0;
  }
`

export default PublicCard