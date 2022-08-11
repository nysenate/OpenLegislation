import React from "react"
import Input from "app/shared/Input";
import { DateTime } from "luxon";
import DatePicker from "app/shared/DatePicker";
import Pagination, { PageParams } from "app/shared/Pagination";
import { searchApiLogs } from "app/apis/logsApi";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { anonymousUrl } from "app/lib/urlUtils";
import { Link } from "react-router-dom";
import Select, { SelectOption } from "app/shared/Select";


const registerReducer = function (state, action) {
  switch (action.type) {
    case "submitForm":
      return {
        ...state,
        ...action.payload,
      }
    case "startSearch":
      return {
        ...state,
        isLoading: true,
      }
    case "searchSuccess":
      return {
        ...state,
        response: action.payload,
        isLoading: false
      }
    case "pageChange":
      return {
        ...state,
        pageParams: action.payload
      }
    default:
      return state
  }
}

const initialState = {
  response: undefined,
  from: DateTime.now().minus({ days: 2 }).startOf("day"),
  to: DateTime.now(),
  term: "",
  isLoading: true,
  sort: undefined,
  pageParams: new PageParams(1, 20),
}

export default function ApiLogSearch({ setHeaderText }) {
  const [ state, dispatch ] = React.useReducer(registerReducer, initialState)

  React.useEffect(() => {
    setHeaderText("API Log Search")
  }, [])

  React.useEffect(() => {
    dispatch({ type: "startSearch" })
    searchApiLogs(state.term || "*", state.from, state.to, state.sort, state.pageParams.limit, state.pageParams.offset)
      .then(res => dispatch({ type: "searchSuccess", payload: res }))
  }, [ state.term, state.from, state.to, state.sort, state.pageParams ])

  const onPageChange = (pageInfo) => {
    dispatch({
      type: "pageChange",
      payload: pageInfo
    })
  }

  return (
    <div className="p-3">
      <div className="mb-3">
        <ApiLogSearchForm term={state.term} from={state.from} to={state.to} sort={state.sort} dispatch={dispatch} />
      </div>
      {state.isLoading &&
        <LoadingIndicator />
      }
      <Pagination currentPage={state.pageParams.selectedPage}
                  limit={state.pageParams.limit}
                  total={state.response?.total}
                  onPageChange={onPageChange} />
      <ApiLogSearchResults response={state.response} />
      <Pagination currentPage={state.pageParams.selectedPage}
                  limit={state.pageParams.limit}
                  total={state.response?.total}
                  onPageChange={onPageChange} />
    </div>
  )
}

function ApiLogSearchResults({ response }) {
  if (!response) {
    return null
  }
  return (
    <div>
      <table className="table table--stripe w-full text text--small">
        <thead>
        <tr>
          <th>Time</th>
          <th>IP</th>
          <th>User</th>
          <th>Method</th>
          <th>Status</th>
          <th>Latency</th>
          <th>Url</th>
        </tr>
        </thead>

        <tbody>
        {response.result.items.map((r, index) => <ApiLogSearchResultRow result={r} key={index} />)}
        </tbody>
      </table>
    </div>
  )
}

function ApiLogSearchResultRow({ result }) {
  return (
    <tr>
      <td>
        {result.result.requestTime}
      </td>
      <td>
        {result.result.ipAddress}
      </td>
      <td>
        {result.result.apiUserName}
      </td>
      <td>
        {result.result.requestMethod}
      </td>
      <td className={result.result.statusCode === 200 ? "" : "text--error"}>
        {result.result.statusCode}
      </td>
      <td className={result.result.processTime < 1000 ? "" : "text--error"}>
        {result.result.processTime} ms
      </td>
      <td>
        <Link to={`${anonymousUrl(result.result.url)}`} target="_blank" className="link">
          {result.result.url}
        </Link>
      </td>
    </tr>
  )
}

function ApiLogSearchForm({ term, from, to, sort, dispatch }) {
  const [ dirtyTerm, setDirtyTerm ] = React.useState(term)
  const [ dirtyFrom, setDirtyFrom ] = React.useState(from)
  const [ dirtyTo, setDirtyTo ] = React.useState(to)
  const [ dirtySort, setDirtySort ] = React.useState(sort)

  const onSubmit = (e) => {
    e.preventDefault()
    dispatch({
      type: "submitForm",
      payload: {
        term: dirtyTerm,
        from: dirtyFrom,
        to: dirtyTo,
        sort: dirtySort
      }
    })
  }

  return (
    <form onSubmit={(e) => onSubmit(e)}>
      <div className="flex gap-x-6">
        <Input label="Search Term"
               value={dirtyTerm}
               onChange={(e) => setDirtyTerm(e.target.value)}
               type="text"
               name="term"
               tabIndex="1" />
        <div>
          <DatePicker label="From"
                      date={dirtyFrom}
                      setDate={(date) => setDirtyFrom(date)}
                      maxDate={dirtyTo}
                      showTimeSelect={true}
                      selectsStart
                      name="from" />
        </div>
        <div>
          <DatePicker label="To"
                      date={dirtyTo}
                      setDate={(date) => setDirtyTo(date)}
                      maxDate={DateTime.now()}
                      showTimeSelect={true}
                      selectsEnd
                      name="to" />
        </div>
        <div>
          <Select label="Sort by"
                  value={sort}
                  options={apiLogSearchSortOptions}
                  onChange={(e) => setDirtySort(e.target.value)}
                  name="sort"
          />
        </div>
      </div>

      <div className="flex justify-end">
        <button className="btn btn--primary w-36" type="submit">Search</button>
      </div>
    </form>
  )
}

const apiLogSearchSortOptions = [
  new SelectOption("requestTime:desc", "Recent Requests"),
  new SelectOption("requestTime:asc", "Older Requests"),
  new SelectOption("processTime:desc", "Longest Latency"),
  new SelectOption("processTime:asc", "Shortest Latency"),
  new SelectOption("statusCode:desc", "Status Code Desc"),
  new SelectOption("statusCode:asc", "Status Code Asc"),
]
