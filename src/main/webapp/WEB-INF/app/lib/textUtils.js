
export function capitalize(word) {
  const lowercase = word.toLowerCase()
  return lowercase.charAt(0).toUpperCase() + lowercase.slice(1)
}