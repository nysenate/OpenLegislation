import React from 'react'
import PublicCard from "./PublicCard";
import SubTitle from "./SubTitle";
import styled from "styled-components";
import Button from "./Button";


export default function BrowseLegislation() {
  const apiKeyRef = React.useRef();

  return (
    <PublicCard>
      <header>
        <SubTitle>Browse Legislation</SubTitle>
      </header>
      <p>
        We have an Open Legislation interface that we use to assist in our development.
      </p>
      <p>
        Feel free to access it by using your API Key.
      </p>
      <BrowseForm>
        <input
          ref={apiKeyRef}
          type="text"
          name="apiKey"
          placeholder="Enter your API key here to browse the data."/>
        <Button type="submit" onClick={() => alert(apiKeyRef.current.value)}>View Legislation</Button>
      </BrowseForm>
    </PublicCard>
  )
}

// TODO Put in its own file?
const BrowseForm = styled.div`
  text-align: center;
  margin-top: 3rem;
  
  & > input {
    border: 0;
    border-bottom: .05rem solid #bfbfbf;
    margin: 0 auto;
    width: 50rem;
    text-align: center;
    font-size: 1.6rem;
    font-weight: 300;
    
    :focus {
      border: 0;
      border-bottom: .2rem solid rgb(63, 81, 181); 
      //outline: .1rem solid rgb(63, 81, 181);
      outline: none
    }
  }
`