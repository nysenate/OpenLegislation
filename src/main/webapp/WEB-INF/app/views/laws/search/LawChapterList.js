import React from 'react'
import { Link } from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";


/**
 *  Display a list of Law Chapter summary information.
 * @param filter Filter the displayed law chapters to those with a name or lawId matching this string.
 */
export default function LawChapterList({ filter = "" }) {
  const [ lawChapters, setLawChapters ] = React.useState([])
  const [ filteredLawChapters, setFilteredLawChapters ] = React.useState([])

  React.useEffect(() => {
    getLawsApi()
      .then((response) => {
        setLawChapters(response.items)
      })
      .catch((error) => {
        console.warn(`${error}`)
      })
  }, [])

  React.useEffect(() => {
    // An empty string will match every string no need to check if the filter has been set.
    setFilteredLawChapters(lawChapters.filter(lawChapter => {
      return lawChapter.name.toUpperCase().includes(filter.toUpperCase())
        || lawChapter.lawId.toUpperCase().includes(filter.toUpperCase())
    }))
  }, [ lawChapters, filter ])

  return (
    <div className="mt-8">
      {filteredLawChapters.map((lawChapter) =>
        <Link to={`/laws/${lawChapter.lawId}`} key={lawChapter.lawId}>
          <LawChapterInfo lawChapter={lawChapter} />
        </Link>
      )}
    </div>
  )
}

function LawChapterInfo({ lawChapter }) {
  return (
    <div className="p-3 hover:bg-gray-200 rounded flex items-center">
      <div className="text w-16 lg:w-36 flex-none">
        <b>{lawChapter.lawId}</b>
      </div>

      <div className="flex-none">
        <div className="text">{lawChapter.name}</div>
        <div className="text text--small overflow-x">{lawChapter.lawType}&nbsp;|&nbsp;Chapter&nbsp;{lawChapter.chapter}</div>
      </div>
    </div>
  )
}
