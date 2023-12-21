import React from 'react'
import { useLocation } from "react-router-dom";
import * as queryString from "query-string";
import ErrorMessage from "app/shared/ErrorMessage";
import ContentContainer from "app/shared/ContentContainer";
import getSubscriptions from "app/apis/subscriptionApi";
import Subscriptions from "app/views/apiUser/Subscriptions";
import Email from "app/views/apiUser/Email";


export default function ApiUserInfo({ setHeaderText }) {
  const [ subscriptions, setSubscriptions ] = React.useState([])
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const [ loading, setLoading] = React.useState(true)
  const location = useLocation()
  const key = queryString.parse(location.search).key

  React.useEffect(() => {
    setHeaderText("Modify Account Settings")
    getSubscriptions(key)
      .then((res) => setSubscriptions(res.result.items))
      .catch(() => setErrorMsg("Invalid API key provided"))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return null
  }

  if (!loading && errorMsg) {
    return (
      <ContentContainer className="p-3">
        <ErrorMessage>{errorMsg}</ErrorMessage>
      </ContentContainer>
    )
  }

  return (
    <ContentContainer>
      <div className="text-center mx-auto">
        <Subscriptions apiKey={key} subscriptions={subscriptions} />
        <hr className="my-6 mx-20"/>
        <Email apiKey={key} />
      </div>
    </ContentContainer>
  )
}
