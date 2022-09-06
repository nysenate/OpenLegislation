import React from "react";
import {
  loginAdmin,
  loginWithApiKey,
  logout
} from "../apis/authApi";
import { DateTime } from "luxon";
import {
  loadAuth,
  saveAuth
} from "app/lib/authStorage";
import { useLocation } from "react-router-dom";


const AuthContext = React.createContext()

function useProvideAuth() {
  // Load any existing auth from local storage. This is necessary to persist
  // the auth across refreshes and new tabs.
  const localStorageAuth = loadAuth()

  // Is an API user logged in.
  const [ isAuthed, setIsAuthed ] = React.useState(localStorageAuth.isAuthed)
  // Is an Admin user logged in.
  const [ isAdmin, setIsAdmin ] = React.useState(localStorageAuth.isAdmin)
  // A ISO datetime string representation of the users last action.
  const [ lastActionDate, setLastActionDate ] = React.useState(localStorageAuth.lastAccessDate)

  // A user's auth is expired if they have been inactive for 30+ minutes.
  const isExpired = () => {
    return DateTime.now() > DateTime.fromISO(lastActionDate).plus({ minutes: 30 })
  }

  return {
    isAuthed() {
      return isAuthed && !isExpired()
    },
    isAdmin() {
      return isAdmin && !isExpired()
    },
    lastActionDate,
    updateLastActionDate(date = DateTime.now()) {
      // Only update the lastActionDate if the auth is not expired.
      // Updating the lastActionDate when expired would incorrectly cause the auth to appear valid.
      if (!isExpired()) {
        setLastActionDate(date.toISO())
      }
    },
    loginApiUser(apiKey) {
      return loginWithApiKey(apiKey)
        .then((res) => {
          setIsAuthed(res.result.isAuthed)
          setLastActionDate(DateTime.now().toISO())
        })
    },
    loginAdminUser(username, password) {
      return loginAdmin(username, password)
        .then((res) => {
          setIsAuthed(res.result.isAuthed)
          setIsAdmin(res.result.isAdmin)
          setLastActionDate(DateTime.now().toISO())
        })
    },
    logoutUser() {
      logout()
        .finally(() => {
          setIsAuthed(false)
          setIsAdmin(false)
        })
    }
  }
}

export function AuthProvider({ children }) {
  const auth = useProvideAuth()
  const location = useLocation()

  // Persist auth to localStorage whenever it is updated.
  React.useEffect(() => {
    saveAuth(auth)
  }, [ auth ])

  // Updates the lastActionDate whenever the user navigates to a new page.
  React.useEffect(() => {
    auth.updateLastActionDate()
  }, [ location ])

  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  )
}

export default function useAuth() {
  return React.useContext(AuthContext)
}
