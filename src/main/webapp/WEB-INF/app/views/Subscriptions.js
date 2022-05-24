import React from 'react'
import {
  Route,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import getSubscriptions from "app/apis/subscriptionApi";

const breakingChanges = "BREAKING_CHANGES"
const newFeatures = "NEW_FEATURES"

export default function Subscriptions( {setHeaderText} ) {
  return (
    <Route exact path = "/subscriptions">
      <SubscriptionView setHeaderText={ setHeaderText }/>
    </Route>
  )
}

function SubscriptionView( {setHeaderText} ) {
  const [ subscriptionSet, setData ] = React.useState(new Set())
  const [ errorMsg, setErrorMsg ] = React.useState("")

  const params = queryString.parse(useLocation().search)
  if (!params.apiKey) {
    return <div>Error! URL must have an API key parameter.</div>
  }
  React.useEffect(() => {
    setHeaderText("Email Subscriptions")
  })

  React.useEffect(() => {
    setErrorMsg("")
    getSubscriptions(params.apiKey)
      .then((data) => setData(new Set(data)))
      .catch((error) => setErrorMsg(error.message))
  }, [ params.apiKey ]);

  return (
    <div>
      <p className="flex justify-center text--large">
        <div>
          <div>
            <Checkbox label={newFeatures} initialValue={subscriptionSet.has(newFeatures)}/>
          </div>
          <div>
            <Checkbox label={breakingChanges} initialValue={subscriptionSet.has(breakingChanges)}/>
          </div>
        </div>
      </p>
    </div>
  )
}

function Checkbox({ label, initialValue }) {
  const [checked, setChecked] = React.useState(initialValue);
  return (
    <label>
      <input type="checkbox"
             checked={checked}
             onChange={() => setChecked(!checked)}/>
      {label}
    </label>
  );
}
