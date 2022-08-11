import React from "react"
import DeleteIcon from "app/shared/DeleteIcon";
import {
  createAdminUser,
  deleteAdminUser,
  fetchAdminAccounts
} from "app/apis/adminAccountApi";
import Input from "app/shared/Input";


export default function ManageAdminUsers({ setHeaderText }) {
  const [ adminUsers, setAdminUsers ] = React.useState([])

  React.useEffect(() => {
    loadAdminAccounts()
    setHeaderText("Manage Admin Users")
  }, [])

  const loadAdminAccounts = () => {
    fetchAdminAccounts()
      .then(res => setAdminUsers(res.result.items))
  }

  const onSubmit = (e, username, isMasterAdmin) => {
    e.preventDefault()
    createAdminUser(username, isMasterAdmin)
      .then(() => loadAdminAccounts())
  }

  const onDelete = user => {
    deleteAdminUser(user.username)
      .then(() => loadAdminAccounts())
  }

  return (
    <div className="m-3">
      <AddAdminUserForm onSubmit={onSubmit} />
      <hr className="my-10" />
      <AdminUserList adminUsers={adminUsers} onDelete={onDelete}/>
    </div>
  )
}

function AddAdminUserForm({ onSubmit }) {
  const [ username, setUsername ] = React.useState("")
  const [ isMasterAdmin, setIsMasterAdmin ] = React.useState(false)

  return (
    <div>
      <h3 className="h4 mb-3">Create an Admin User</h3>
      <form onSubmit={e => onSubmit(e, username, isMasterAdmin)} className="flex gap-6 items-end">
        <Input label="Username" value={username} onChange={e => setUsername(e.target.value)} />
        <div>
          <input type="checkbox"
                 defaultChecked={isMasterAdmin}
                 onChange={e => setIsMasterAdmin(e.target.checked)}
                 className="cursor-pointer"
                 name="isMasterAdmin"
                 id="isMasterAdmin" />
          <label htmlFor="isMasterAdmin" className="label cursor-pointer m-2">Master Admin</label>
        </div>
        <div>
          <button className="btn btn--primary w-36" type="submit">Create Admin</button>
        </div>
      </form>
    </div>
  )
}

function AdminUserList({ adminUsers, onDelete }) {
  return (
    <div className="my-6">
      <h3 className="h4">Current Admin Users</h3>
      <table className="table table--stripe w-full my-3">
        <thead>
        <tr>
          <th>Username</th>
          <th>Role</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        {adminUsers.map((user) =>
          <tr key={user.username}>
            <td>{user.username}</td>
            {user.master === true
              ? <td>Master Admin</td>
              : <td></td>}
            <td><DeleteIcon onClick={() => onDelete(user)}/></td>
          </tr>
        )}
        </tbody>
      </table>

    </div>
  )
}