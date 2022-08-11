import React from 'react'
import Input from "app/shared/Input";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string"


export default function LawSearchForm({ term = "", filter = "" }) {
  const [ dirtyTerm, setDirtyTerm ] = React.useState(term)
  const [ dirtyFilter, setDirtyFilter ] = React.useState(filter)
  const { search } = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    setDirtyTerm(term)
    setDirtyFilter(filter)
  }, [ term, filter ])

  const onSubmit = e => {
    e.preventDefault()
    const params = queryString.parse(search)
    params.term = dirtyTerm
    params.chapterFilter = dirtyFilter
    params.page = 1
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div className="flex">
          <div className="flex-1 mr-6">
            <Input label="Search laws by term"
                   onChange={(e) => setDirtyTerm(e.target.value)}
                   value={dirtyTerm}
                   tabIndex="1"
                   name="lawsearch"
                   type="text"
                   placeholder="e.g. official state muffin , STL 84"
                   className="w-full" />
          </div>
          <div className="flex-none">
            <Input label="Filter by law chapter"
                   onChange={(e) => setDirtyFilter(e.target.value)}
                   value={dirtyFilter}
                   tabIndex="2"
                   name="chapterFilter"
                   type="text"
                   placeholder="e.g. TAX" />
          </div>
        </div>
        <div className="flex justify-end">
          <button className="btn btn--primary my-3 w-36" type="submit" tabIndex="3">Search</button>
        </div>
      </form>
    </div>
  )
}