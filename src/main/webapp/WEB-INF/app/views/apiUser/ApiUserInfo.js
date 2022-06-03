import React from 'react'
import { useLocation } from "react-router-dom";
import * as queryString from "query-string";
import Subscriptions from "app/views/apiUser/Subscriptions";
import Email from "app/views/apiUser/Email";

export default function ApiUserInfo({setHeaderText} ) {
  const params = queryString.parse(useLocation().search)
  if (!params.apiKey) {
    return <div>Error! URL must have an API key parameter.</div>
  }
  React.useEffect(() => setHeaderText("User Information"))
  return (
    <div>
      <Subscriptions apiKey={params.apiKey}/>
      <Email apiKey={params.apiKey}/>
    </div>
  )
}
