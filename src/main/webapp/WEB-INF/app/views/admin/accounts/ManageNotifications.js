import React from "react"
import {
  fetchNotificationTypes,
  fetchUserNotificationSubscriptions,
  subscribeApi,
  unsubscribeFromNotification
} from "app/apis/adminAccountApi";
import Select, { SelectOption } from "app/shared/Select";
import Input from "app/shared/Input";
import DeleteIcon from "app/shared/DeleteIcon";


export default function ManageNotifications({ setHeaderText }) {
  const [ subscriptions, setSubscriptions ] = React.useState([])
  const [ notificationTypes, setNotificationTypes ] = React.useState([])
  const [ notificationMediums, setNotificationMediums ] = React.useState([])

  React.useEffect(() => {
    getUserSubscriptions()
    getNotificationTypes()
    setHeaderText("Manage Notification Settings")
  }, [])

  const getUserSubscriptions = () => {
    fetchUserNotificationSubscriptions()
      .then((res) => setSubscriptions(res.result.items))
  }

  const getNotificationTypes = () => {
    fetchNotificationTypes()
      .then((res) => {
        setNotificationTypes(res.result.notificationTypes)
        setNotificationMediums(res.result.notificationMediums)
      })
  }

  const unsubscribe = (subscription) => {
    unsubscribeFromNotification(subscription.id)
      .then(() => getUserSubscriptions())
  }

  return (
    <div className="p-3">
      <SubscribeForm notificationTypes={notificationTypes}
                     notificationMediums={notificationMediums}
                     onSubscribe={() => getUserSubscriptions()} />
      <hr className="my-10" />
      <CurrentSubscriptions subscriptions={subscriptions} onUnsubscribe={unsubscribe} />
    </div>
  )
}

function SubscribeForm({ notificationTypes, notificationMediums, onSubscribe }) {
  const [ type, setType ] = React.useState()
  const [ typeOptions, setTypeOptions ] = React.useState()
  const [ medium, setMedium ] = React.useState()
  const [ mediumOptions, setMediumOptions ] = React.useState()
  const [ address, setAddress ] = React.useState("")

  React.useEffect(() => {
    const emptyOption = new SelectOption("", "")
    setTypeOptions([ emptyOption ].concat(notificationTypes.map((t) => new SelectOption(t, t))))
    setMediumOptions([ emptyOption ].concat(notificationMediums.map((m) => new SelectOption(m, m))))
  }, [ notificationTypes, notificationMediums ])

  const subscribe = (e) => {
    e.preventDefault()
    subscribeApi(type, medium, address)
      .then(() => onSubscribe())
  }

  return (
    <div>
      <h3 className="h4 mb-3">Add New Subscription</h3>
      <form onSubmit={(e) => subscribe(e)} className="flex gap-6 items-end">
        <Select label="Notification Type"
                value={type}
                options={typeOptions}
                onChange={(e) => setType(e.target.value)}
                name="type" />
        <Select label="Notification Medium"
                value={medium}
                options={mediumOptions}
                onChange={(e) => setMedium(e.target.value)}
                name="medium" />
        <Input label="Address"
               value={address}
               onChange={(e) => setAddress(e.target.value)}
               name="address" />
        <div>
          <button className="btn btn--primary w-36" type="submit">Subscribe</button>
        </div>
      </form>
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
        </tr>
        </thead>
        <tbody>
        {subscriptions.map((sub) => (
          <tr key={sub.id}>
            <td>{sub.type}</td>
            <td>{sub.target}</td>
            <td>{sub.address}</td>
            <td>
              <DeleteIcon onClick={() => onUnsubscribe(sub)} />
            </td>
          </tr>
        ))}
        </tbody>
      </table>
    </div>
  )
}
