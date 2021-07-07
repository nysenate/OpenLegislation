import "core-js/stable";
import "regenerator-runtime/runtime";

// TODO add more search options for this endpoint
export default async function billSearch(term) {
  const response = await fetch(`/api/3/bills/search?` + new URLSearchParams({
    term: term
  }))
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data
}