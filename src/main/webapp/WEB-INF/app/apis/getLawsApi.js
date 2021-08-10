import "core-js/stable";
import "regenerator-runtime/runtime";

export default function getLawsApi(lawId, locationId) {
    let url = `/api/3/laws/`
    if (lawId) {
        url += `/${lawId}`
    }
    if (locationId) {
        url += `/${locationId}`
    }
    return fetchUrl(url)
}

async function fetchUrl(url) {
    const response = await fetch(url)
    const data = await response.json()
    if (!data.success) {
        throw new Error(data.message)
    }
    return data.result
}