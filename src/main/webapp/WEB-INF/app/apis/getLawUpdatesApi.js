import "core-js/stable";
import "regenerator-runtime/runtime";
import * as queryString from "query-string";

export default async function lawSearchApi(detail, from, to, order, type, limit = 6, offset = 1) {
    const response = await fetch(`/api/3/laws/updates/` + from + '/' + to + '?' + queryString.stringify({
        detail: detail,
        limit: limit,
        offset: offset,
        order: order,
        type: type
    }))
    const data = await response.json()
    if (!data.success) {
        throw new Error(data.message)
    }
    return data
}