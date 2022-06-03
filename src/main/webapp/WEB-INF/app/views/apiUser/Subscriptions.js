import React from "react";
import getSubscriptions, { updateSubscriptions } from "app/apis/subscriptionApi";
import { capitalizePhrase } from "app/lib/textUtils";

const BREAKING_CHANGES = "BREAKING_CHANGES"
const NEW_FEATURES = "NEW_FEATURES"

export default function Subscriptions({ apiKey }) {
  const [ changesNotif, setChangesNotif ] = React.useState(false)
  const [ newFeaturesNotif, setNewFeaturesNotif ] = React.useState(false)
  const [ message, setMessage ] = React.useState("")

  React.useEffect(() => {
    getSubscriptions(apiKey)
      .then((data) => {
        setChangesNotif(data.includes(BREAKING_CHANGES))
        setNewFeaturesNotif(data.includes(NEW_FEATURES))
      })
  }, [apiKey]);

  const onSubmit = (e) => {
    e.preventDefault()
    const subs = []
    if (changesNotif) {
      subs.push(BREAKING_CHANGES)
    }
    if (newFeaturesNotif) {
      subs.push(NEW_FEATURES)
    }
    updateSubscriptions(apiKey, subs)
    setMessage("Subscriptions updated!")
  }

  return (
    <form className="flex justify-center text--large" onSubmit={onSubmit}>
      <div>
        <div>Check all email subscriptions you would like to receive:</div>
        <div>
          <Checkbox name={BREAKING_CHANGES} checked={changesNotif}
                    setChecked={setChangesNotif}/>
        </div>
        <div>
          <Checkbox name={NEW_FEATURES} checked={newFeaturesNotif}
                    setChecked={setNewFeaturesNotif}/>
        </div>
        <button className="btn btn--primary my-3 w-55" type="submit">Update subscriptions</button>
        <div>{message}</div>
      </div>
    </form>
  )
}

function Checkbox({ name, checked, setChecked }) {
  return (
    <label>
      <input name={name}
             type="checkbox"
             checked={checked}
             onChange={() => setChecked(!checked)}/>
      {capitalizePhrase(name.replaceAll("_", " "))}
    </label>
  );
}
