import React from 'react';
import getLawsApi from "app/apis/getLawsApi";
import {
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import * as queryString from "query-string";
import LoadingIndicator from "app/shared/LoadingIndicator";
import SpecifiedLawArticles from "app/views/laws/SpecifiedLawArticles";
import SpecifiedLawSearchForm from "app/views/laws/SpecifiedLawSearchForm";

export default function SpecifiedLaw({ setHeaderText }) {
  const [ response, setResponse ] = React.useState({ result: { items: [] } })
  const [ loading, setLoading ] = React.useState(true)
  const [ term, setTerm ] = React.useState("*")
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search)
  const limit = 6
  const match = useRouteMatch()

  React.useEffect(() => {
    getLaw(match.params.lawId)
  }, [])

  React.useEffect(() => {

  }, [ term ])

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
      <SpecifiedLawSearchForm searchTerm={setTerm} />
      {loading
        ? <LoadingIndicator />
        : <SpecifiedLawArticles response={response}
                                term={term}
                                limit={limit}
                                page={params.page}
                                onPageChange={onPageChange} />
      }
    </div>
  )

}