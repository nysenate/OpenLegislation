import "core-js/stable";
import "regenerator-runtime/runtime";

export async function getMembersApi(session) {
  let url = session ? `/api/3/members/${session}` : '/api/3/members'
  url += "?full=true&limit=1000"
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data.result
}