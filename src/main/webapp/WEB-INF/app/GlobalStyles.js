import {createGlobalStyle} from 'styled-components'
import {normalize} from 'styled-normalize'

const GlobalStyle = createGlobalStyle`
  ${normalize}
 
  // You can continue writing global styles here
  body {
    height: 100%;
    // TODO are any of these Helvetica fonts install on Windows/Linux?
    font-family: "HelveticaNeue-Light", "Helvetica Neue Light", "Helvetica Neue", Helvetica, Arial, "Lucida Grande", sans-serif;
    font-weight: 300;
    font-size: 16px;
    color: #333;
    background: #f5f5f5;
     
    p {
      line-height: 1.6;
    }
    
    address {
      font-style: normal
    }
    
    a {
      color: #008cba;
    }
  }
  
  * {
    box-sizing: border-box;
  }
`

export default GlobalStyle;