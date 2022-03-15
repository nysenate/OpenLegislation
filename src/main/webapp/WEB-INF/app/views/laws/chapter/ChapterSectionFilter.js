import React from 'react'
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import Input from "app/shared/Input";

export default function ChapterSectionFilter({ setTerm }) {
  const [ dirtyTerm, setDirtyTerm ] = React.useState("")
  const location = useLocation()
  const history = useHistory()

  // Update search fields when back/forward navigation is used.
  React.useEffect(() => {
    const params = queryString.parse(location.search)
    setDirtyTerm(params.term || "")
  }, [ location ])

  // Updates the term query param when the form is submitted.
  const onSubmit = (e) => {
    e.preventDefault()
    console.log(location)
    const params = queryString.parse(location.search)
    params.term = dirtyTerm
    setTerm(dirtyTerm)
  }

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div>
          <Input label="Navigate by section number"
                 value={dirtyTerm}
                 onChange={(e) => setDirtyTerm(e.target.value)}
                 placeholder="e.g. 32.01"
                 name="specifiedlawsearch"
                 tabIndex="1"
                 className="w-full" />
        </div>

        <div className="flex justify-end">
          <button className="btn btn--primary my-3 w-36" type="submit" tabIndex="2">Search</button>
        </div>
      </form>
    </div>
  )
}