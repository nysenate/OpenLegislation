import styled from "styled-components";
import Card from "app/components/card";

export const HomePage = styled.div`
  background: #f1f1f1;
  overflow: auto;
`

export const PublicHeader = styled.section`
  margin-bottom: 0;
  height: 30rem;
  background: #008cba;
  position: relative;
`

export const Title = styled.h1`
  margin: 0;
  font-family: 'Roboto Slab', 'sans-serif';
  font-weight: 400;
  color: #fff;
  line-height: 20rem;
  font-size: 8rem;
  text-align: center;
  text-shadow: 0 0 0.1rem #235674;
`

export const SenateSealLogo = styled.img`
  height: 12rem;
  vertical-align: text-top;
  margin-right: 2rem;
`

export const AboutCard = styled(Card)`
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

export const PublicCard = styled(Card)`
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

export const SubTitle = styled.h3`
  margin: 0 auto 2rem 0;
  font-size: 2.4rem;
  font-weight: 400;
  text-align: center;
`

export const ApiKeyFormContainer = styled.div`
  text-align: center;
  margin-top: 3rem;
  
  & input {
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
  
  & > button {
    font-weight: 600;
  }
`

export const Button = styled.button`
  width: 15rem;
  color: rgba(255,255,255,0.87);
  box-shadow: 0 .2rem .5rem 0 rgba(0,0,0,.26);
  background-color: rgb(57,73,171);
  font-weight: 500;
  font-size: 1.4rem;
  cursor: pointer;
  min-height: 3.6rem;
  text-align: center;
  border-radius: .3rem;
  border: 0;
  margin: .6rem 1.6rem;
  padding: 0 .6rem;
  text-transform: uppercase;  
`

export const DataContainer = styled.div`
  display:flex;
  align-items: center;
  justify-content: flex-start;
  padding: 1.6rem;
  flex-wrap: wrap;
`

export const DataTypeIcon = styled.span`
  font-size: 3rem;
  line-height: 7.5rem;
  color: white;
  padding: 1.5rem;
  //width: 7.5rem;
  //height: auto;
`
