import React from "react";
import {
  useHistory,
  useLocation
} from "react-router-dom";
import getSessionApi from "../../apis/getSessionApi";

export default function SessionTranscript() {
  const [response, setResponse] = React.useState({result: {items: []}})
  const location = useLocation()
  const history = useHistory()

  React.useEffect(() => {doInitialSearch()})

  const doInitialSearch = () => {
    getSessionApi(null, null)
      .then((response) => {
        setResponse(response)
      })
  }
}
