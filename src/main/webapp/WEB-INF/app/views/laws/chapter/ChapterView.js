import React from 'react';
import getLawsApi from "app/apis/getLawsApi";
import {
  useLocation,
  useHistory,
  useRouteMatch
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import ArticleList from "app/views/laws/chapter/ArticleList";
import ChapterSectionFilter from "app/views/laws/chapter/ChapterSectionFilter";


export default function ChapterView({ setHeaderText }) {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const [ term, setTerm ] = React.useState("*")
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const limit = 6
  const match = useRouteMatch()

  console.log(response)

  React.useEffect(() => {
    getLaw(match.params.lawId)
  }, [])

  const getLaw = (lawId) => {
    setLoading(true)
    getLawsApi(lawId, null)
      .then((response) => {
        setResponse(response)
        setHeaderText(response.info.name)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`)
      })
      .finally(() => {
        setLoading(false)
      })
  }

  const onPageChange = pageInfo => {
    params.page = pageInfo.selectedPage
    history.push({ search: queryString.stringify(params) })
  }

  return (
    <div className="p-3">
      <ChapterSectionFilter setTerm={setTerm} />
      {loading
        ? <LoadingIndicator />
        : <ArticleList articles={response.documents.documents.items}
                       term={term}
                       limit={limit}
                       page={params.page}
                       onPageChange={onPageChange} />
      }
    </div>
  )
}
