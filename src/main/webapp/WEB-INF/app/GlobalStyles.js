import {createGlobalStyle} from 'styled-components'
import {normalize} from 'styled-normalize'

const GlobalStyle = createGlobalStyle`
  ${normalize}
 
  // You can continue writing global styles here
  body {
    height: 100%;
    font-size: 16px;
    font-family: 'Roboto Slab', 'Open Sans', 'sans-serif';
    background: #f5f5f5;
    color: #444;
     
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