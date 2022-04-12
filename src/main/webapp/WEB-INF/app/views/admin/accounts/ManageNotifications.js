import React from "react"
import {
  fetchUserNotificationSubscriptions,
  unsubscribeFromNotification
} from "app/apis/adminAccountApi";
import { Trash } from "phosphor-react";


export default function ManageNotifications() {
  const [ subscriptions, setSubscriptions ] = React.useState([])

  React.useEffect(() => {
    getUserSubscriptions()
  }, [])

  const getUserSubscriptions = () => {
    fetchUserNotificationSubscriptions()
      .then((res) => setSubscriptions(res.result.items))
  }

  const unsubscribe = (subscription) => {
    unsubscribeFromNotification(subscription.id)
      .then(() => getUserSubscriptions())
  }

  return (
    <div className="p-3">
      Manage notifications
      <hr />
      <CurrentSubscriptions subscriptions={subscriptions} onUnsubscribe={unsubscribe} />
    </div>
  )
}

function CurrentSubscriptions({ subscriptions, onUnsubscribe }) {
  return (
    <div className="my-6">
      <h3 className="h4">Current Notification Subscriptions</h3>
      <table className="table table--stripe w-full my-3">
        <thead>
        <tr>
          <th>Notification Type</th>
          <th>Target Type</th>
          <th>Target Address</th>
          <th></th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        {subscriptions.map((sub) => (
          <tr key={sub.id}>
            <td>{sub.type}</td>
            <td>{sub.target}</td>
            <td>{sub.address}</td>
            <td>
              <DeleteIcon onUnsubscribe={() => onUnsubscribe(sub)} />
            </td>
          </tr>
        ))}
        </tbody>
      </table>
    </div>
  )
}

function DeleteIcon({ onUnsubscribe }) {
  return (
    <Trash onClick={() => onUnsubscribe()}
           className="text-red-600 hover:cursor-pointer inline mr-3"
           size="1.2rem"
           weight="bold" />
  )
}
