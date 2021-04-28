import styled from "styled-components";
import Card from "app/components/card";

export const HomePage = styled.div`
  background: #f1f1f1;
  overflow: auto;
`

export const PublicHeader = styled.section`
  margin-bottom: 0;
  height: 300px;
  background: #008cba;
  position: relative;
`

export const Title = styled.h1`
  margin: 0;
  font-family: 'Roboto Slab', 'sans-serif';
  font-weight: 400;
  color: #fff;
  line-height: 200px;
  font-size: 80px;
  text-align: center;
  text-shadow: 0 0 1px #235674;
`

export const SubTitle = styled.h3`
  margin: 0 auto 20px 0;
  font-size: 24px;
  font-weight: 400;
  text-align: center;
`

export const TitleSmall = styled.h4`
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  text-align: left;
  color: rgba(0,0,0,0.87);
`

export const SenateSealLogo = styled.img`
  height: 120px;
  vertical-align: text-top;
  margin-right: 20px;
`

export const AboutCard = styled(Card)`
  position: relative;
  max-width: 960px;
  padding: 50px 30px;
  margin: -100px auto 0 auto;
  font-weight: 400;
  font-size: 19px;
  text-align: center;
  
  & > a {
    color: #008cba;
  }
`

export const PublicCard = styled(Card)`
  margin: 50px auto 0 auto;
  padding: 20px 20px;
  max-width: 960px;
  font-size: 19px;
  font-weight: 300;
  text-align: center;
  
  & > p {
    margin: 10px 0;
  }
`

export const ApiKeyFormContainer = styled.div`
  text-align: center;
  margin-top: 30px;
  
  & input {
    border: 0;
    border-bottom: .5px solid #bfbfbf;
    margin: 0 auto;
    width: 500px;
    text-align: center;
    font-size: 16px;
    font-weight: 300;
    
    :focus {
      border: 0;
      border-bottom: 2px solid rgb(63, 81, 181); 
      //outline: 1px solid rgb(63, 81, 181);
      outline: none
    }
  }
  
  & > button {
    font-weight: 600;
  }
`

export const Button = styled.button`
  width: 150px;
  color: rgba(255,255,255,0.87);
  box-shadow: 0 2px 5px 0 rgba(0,0,0,.26);
  background-color: rgb(57,73,171);
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  min-height: 36px;
  text-align: center;
  border-radius: 3px;
  border: 0;
  margin: 6px 16px;
  padding: 0 6px;
  text-transform: uppercase;  
`

export const DataContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
`

export const DataCard = styled.div`
  display: flex;
  justify-content: flex-start;
  padding: 16px;
  width: 460px;
  cursor: pointer;
  :hover {
    background: rgba(158,158,158,0.2);
  }
  transition: background-color 0.4s ease;
`

export const DataTypeIcon = styled.span`
  font-size: 30px;
  line-height: 55px;
  color: white;
  padding: 15px;
  margin-right: 15px;
`

export const Paragraph = styled.p`
  font-size: 14px;
  text-align: left;
  font-weight: 400;
`
