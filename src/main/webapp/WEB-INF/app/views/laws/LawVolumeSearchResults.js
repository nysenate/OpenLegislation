import React from 'react'
import { Link } from "react-router-dom";

export default function LawVolumeSearchResults({ response, filter }) {

  if (response.items.length === 0) {
    return (
      <div>
        No results found
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="pt-3">
        <ResultList results={response.items} filter={filter} />
      </div>
    </div>
  )
}

function ResultList({ results, filter }) {
  return (
    <div>
      {results.map((r) =>
        <Link to={`/laws/${r.lawId}`}
              key={r.lawId}>
          <ResultItem result={r} filter={filter} key={r.lawId} />
        </Link>
      )}
    </div>
  )
}

function ResultItem({ result, filter }) {
  const volume = result
  const doFilterResult = doFilter(result, filter)
  return (
    <div>
      {doFilterResult &&
        <div className="p-3 hover:bg-gray-200 rounded flex items-center">
          <div className="text w-16 lg:w-36 flex-none">
            <b>{volume.lawId}</b>
          </div>

          <div className="flex-none">
              <div className="text">{volume.name}</div>
              <div className="text text--small">{volume.lawType}&nbsp;|&nbsp;Chapter&nbsp;{volume.chapter}</div>
          </div>
        </div>
      }
    </div>
  )
}

function doFilter(result, filter) {
  if (filter === undefined || isEmpty(filter)) {
    return true;
  }
  return result.name.toLowerCase().includes(filter.toLowerCase()) || result.lawId.toLowerCase().includes(filter.toLowerCase());
}

function isEmpty(str) {
  return (!str || str.length === 0);
}