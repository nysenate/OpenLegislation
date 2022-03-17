/**
 * Capitalizes the first character of 'text', all other characters are converted to lower case.
 * @param text
 * @returns {string}
 */
export function capitalize(text) {
  if (!text) {
    return ""
  }
  const lowercase = text.toLowerCase()
  return lowercase.charAt(0).toUpperCase() + lowercase.slice(1)
}

/**
 * Capitalizes the first letter of each word in 'text'.
 * @param text
 */
export function capitalizePhrase(text) {
  const words = text.split(" ")
  const capitalizedWordsArray = words.map((w) => capitalize(w))
  return capitalizedWordsArray.join(" ")
}
