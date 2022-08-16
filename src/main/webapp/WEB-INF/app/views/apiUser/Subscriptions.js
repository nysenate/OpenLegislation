import React from "react";
import { updateSubscriptions } from "app/apis/subscriptionApi";
import { Checkbox } from "app/shared/Checkbox";

const BREAKING_CHANGES = "BREAKING_CHANGES"
const NEW_FEATURES = "NEW_FEATURES"

export default function Subscriptions({ apiKey, subscriptions }) {
  const [ subToBreakingChanges, setSubToBreakingChanges ] = React.useState(subscriptions.includes(BREAKING_CHANGES))
  const [ subToNewFeatures, setSubToNewFeatures ] = React.useState(subscriptions.includes(NEW_FEATURES))
  const [ message, setMessage ] = React.useState("")

  const onSubmit = (e) => {
    e.preventDefault()
    const subs = []
    if (subToBreakingChanges) {
      subs.push(BREAKING_CHANGES)
    }
    if (subToNewFeatures) {
      subs.push(NEW_FEATURES)
    }
    updateSubscriptions(apiKey, subs)
      .then(() => setMessage("Subscriptions updated!"))
      .catch(() => setMessage("Error updating subscriptions."))
  }

  return (
    <form className="flex justify-center text--large my-3" onSubmit={onSubmit}>
      <div>
        <h3 className="h5 mb-3">Check all email subscriptions you would like to receive:</h3>
        <Checkbox label="Breaking changes to the API"
                  value={subToBreakingChanges}
                  onChange={e => setSubToBreakingChanges(e.target.checked)}
                  name="breakingChanges" />
        <Checkbox label="New features added to the API"
                  value={subToNewFeatures}
                  onChange={e => setSubToNewFeatures(e.target.checked)}
                  name="newFeatures" />
        <button className="btn btn--primary my-3 w-55" type="submit">Update subscriptions</button>
        <div>{message}</div>
      </div>
    </form>
  )
}
