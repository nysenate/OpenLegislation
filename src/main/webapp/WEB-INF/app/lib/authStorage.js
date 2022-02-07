/**
 * Utility functions for saving and loading auth data to local storage.
 *
 * We must persist this data outside react state so that it is persisted
 * through the user reloading a page or opening a new tab.
 */
import { DateTime } from "luxon";

const isAuthedKey = "auth.isAuthed"
const isAdminKey = "auth.isAdmin"
const lastActionKey = "auth.lastAccessDate"

export function saveAuth(auth) {
  localStorage.setItem(isAuthedKey, JSON.stringify(auth.isAuthed()) || false)
  localStorage.setItem(isAdminKey, JSON.stringify(auth.isAdmin()) || false)
  localStorage.setItem(lastActionKey, auth.lastActionDate)
}

export function loadAuth() {
  const isAuthed = JSON.parse(localStorage.getItem(isAuthedKey)) || false
  const isAdmin = JSON.parse(localStorage.getItem(isAdminKey)) || false
  const lastAccessDate = localStorage.getItem(lastActionKey) || DateTime.now().toISO()
  return {
    isAuthed,
    isAdmin,
    lastAccessDate
  }
}
