import "core-js/stable";
import "regenerator-runtime/runtime";

export default function billGetApi(sessionYear, printNo) {
  return printNo ? fetchBill(sessionYear, printNo)
    : fetchSession(sessionYear);
}

function fetchBill(sessionYear, printNo) {
  return fetchUrl(`/api/3/bills/${sessionYear}/${printNo}`)
}

function fetchSession(sessionYear) {
  return fetchUrl(`/api/3/bills/${sessionYear}`)
}

async function fetchUrl(url) {
  const response = await fetch(url)
  const data = await response.json()
  if (!data.success) {
    throw new Error(data.message)
  }
  return data.result
}
