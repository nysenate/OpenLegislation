# OpenLegislation

The NY Senate's legislative data platform: it ingests LBDC's legislative data and serves it through
a public JSON API and a web UI.

## Public API

**Response envelope**:
The top-level fields every JSON response from `/api/3` has: `success`, `message`, `responseType` and
`result`.

**Error response**:
A response envelope with `success` set to `false`, `responseType` set to `"error"`, and an
`errorCode` saying what went wrong, sometimes with `errorData`.
_Avoid_: error envelope, JSON error envelope, error page
