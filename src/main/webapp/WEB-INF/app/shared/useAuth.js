import React from "react";
import {
  loginAdmin,
  loginWithApiKey
} from "../apis/authApi";
import { DateTime } from "luxon";


const AuthContext = React.createContext()

function useProvideAuth() {
  // If current time is before the expiration time, initialize with values from local storage,
  // otherwise reset auth's to false.
  const isExpired = DateTime.now() > DateTime.fromISO(localStorage.getItem("auth.expires"));
  const initIsAuthed = isExpired ? false : localStorage.getItem("auth.isAuthed") === "true";
  const initIsAdmin = isExpired ? false : localStorage.getItem("auth.isAdmin") === "true";

  // Is an API user logged in.
  const [ isAuthed, setIsAuthed ] = React.useState(initIsAuthed)
  // Is an Admin user logged in.
  const [ isAdmin, setIsAdmin ] = React.useState(initIsAdmin)

  React.useEffect(() => {
    // Saves auth info to local storage, so it can be persisted across page reloads.
    localStorage.setItem("auth.isAuthed", isAuthed ? "true" : "false")
    localStorage.setItem("auth.isAdmin", isAdmin ? "true" : "false")
    // Set an expiration date. After this date the saved auth data will be ignored.
    localStorage.setItem("auth.expires", DateTime.now().plus({ hour: 1 }).toISO())
  }, [ isAuthed, isAdmin ])

  return {
    isAuthed,
    isAdmin,
    loginApiUser(apiKey) {
      return loginWithApiKey(apiKey)
        .then((res) => {
          setIsAuthed(res.result.isAuthed)
        })
    },
    loginAdminUser(username, password) {
      return loginAdmin(username, password)
        .then((res) => {
          setIsAuthed(res.result.isAuthed)
          setIsAdmin(res.result.isAdmin)
        })
    }
  }
}

export function AuthProvider({ children }) {
  const auth = useProvideAuth()

  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  )
}

export default function useAuth() {
  return React.useContext(AuthContext)
}
