import styled from "styled-components";
import Card from "app/components/card";

export const HomePage = styled.div`
  background: #f1f1f1;
  overflow: auto;
`

export const PublicHeader = styled.section`
  margin-bottom: 0;
  height: 300px;
  //background: #008cba;
  background: ${props => props.theme.colors.blue5};
  position: relative;
`

export const SenateSealLogo = styled.img`
  height: 110px;
  vertical-align: text-top;
  margin-right: 20px;
`

export const Title = styled.h1`
  margin: 0;
  font-weight: 400;
  color: #fff;
  line-height: 200px;
  font-size: 80px;
  text-align: center;
  text-shadow: 0 0 1px #235674;
`

export const PublicWrapper = styled.div`
  margin: 0 auto;
  max-width: 960px;
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
  font-family: Open Sans,sans-serif;
`

export const PublicCard = styled(Card)`
  margin-top: 50px;
  padding: 20px 20px;
  font-weight: 400;
  text-align: center;
  
  & p {
    font-weight: 100;
    margin: 10px 0;
  }
`

export const AboutCard = styled(PublicCard)`
  position: relative;
  padding: 50px 30px;
  margin: -100px auto 0 auto;
  font-size: 19px;
`

export const ApiKeyFormContainer = styled.div`
  margin-top: 30px;
`

export const Input = styled.input`
  width: ${props => props.width ? props.width : "100px"};
  border: 0;
  border-bottom: .5px solid #bfbfbf;
  margin: 0 10px;
  text-align: center;
  font-size: 16px;
  font-weight: 300;
  :focus {
    border: 0;
    border-bottom: 2px solid rgb(63, 81, 181); 
    outline: none
  }
`

export const Button = styled.button`
  width: 150px;
  color: #ffffff;
  box-shadow: 0 2px 5px 0 rgba(0,0,0,.26);
  background-color: ${props => props.theme.colors.blue6};
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  min-height: 36px;
  text-align: center;
  border-radius: 3px;
  border: 0;
  margin: 6px 16px;
  padding: 0 6px;
`

export const DataProvidedContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
`

export const DataProvidedListItem = styled.div`
  display: flex;
  justify-content: flex-start;
  padding: 16px;
  width: 460px;
  cursor: pointer;
  :hover {
    background: rgba(158,158,158,0.2);
  }
  transition: background-color 0.4s ease;
  & p {
    font-size: 14px;
    text-align: left;
    font-weight: 300;  
  }
`

export const DataTypeIcon = styled.span`
  font-size: 30px;
  line-height: 55px;
  color: white;
  padding: 15px;
  margin-right: 15px;
  background-color: ${props => props.bgColor}
`

export const Column = styled.div`
  display: flex;
  flex-direction: column;
  
  & > div {
    margin: 10px;
  }
`

export const DocsIframe = styled.iframe`
  height: 95vh;
  border: 0;
  overflow: visible; 
  margin-top: 20px;
`

export const Center = styled.div`
  margin: 20px;
  text-align: center;
`

export const TextSmall = styled.span`
  font-size: 14px;
  font-weight: 100; 
`