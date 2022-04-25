import React from 'react'
import { getBillApi, } from "app/apis/billGetApi";
import LoadingIndicator from "app/shared/LoadingIndicator";
import { Link } from "react-router-dom";

export default function BillFullTextTab({ bill, selectedAmd }) {
  // A fully populated bill object which includes full text in plan and html format (if available) for each amendment.
  const [ fullBill, setFullBill ] = React.useState()

  React.useEffect(() => {
    getBillApi(bill.session, bill.printNo, { view: "with_refs", fullTextFormat: [ "PLAIN", "HTML" ] })
      .then((res) => {
        setFullBill(res.result)
      })
  }, [ bill ])

  if (!fullBill) {
    return <LoadingIndicator />
  }

  return (
    <section className="m-5">
      <header>
        <h3 className="h5 inline">Full Text</h3>
      </header>
      <FullText amd={fullBill.amendments.items[selectedAmd]} />
    </section>
  )
}

const htmlText = "HTML"
const plainText = "PLAIN"

const initialState = {
  textEl: undefined,
  selectedTextType: plainText
}

const registerReducer = function (state, action) {
  switch (action.type) {
    case 'showHtml':
      return {
        ...state,
        selectedTextType: htmlText,
        textEl: <pre className="text text--small" dangerouslySetInnerHTML={{ __html: action.amd.fullTextHtml }} />
      }
    case 'showPlain':
      return {
        ...state,
        selectedTextType: plainText,
        textEl: <pre className="text text--small">{action.amd.fullText}</pre>
      }
    default:
      console.error("Invalid action type")
  }
}

function FullText({ amd }) {
  const [ hasHtmlText, setHasHtmlText ] = React.useState(false)
  const [ state, dispatch ] = React.useReducer(registerReducer, initialState)

  React.useEffect(() => {
    if (amd.fullTextHtml) {
      setHasHtmlText(true)
      // Default to showing html if available
      dispatch({ type: "showHtml", amd: amd })
    } else {
      dispatch({ type: "showPlain", amd: amd })
    }
  }, [ amd ])

  return (
    <div>
      <div className="mt-2">
        <span className="font-semibold text text--small mr-1">Text available as: </span>
        <div className="text text--small inline">
          {state.selectedTextType === htmlText &&
          <div className="inline">
            <span>HTML </span><span className="text-gray-400">| </span>
          </div>
          }
          {(state.selectedTextType !== htmlText && hasHtmlText) &&
          <div className="inline">
            <span><a onClick={() => dispatch({ type: "showHtml", amd: amd })} className="link">HTML</a> </span>
            <span className="text-gray-400">| </span>
          </div>
          }
          {state.selectedTextType === plainText &&
          <div className="inline">
            <span>TXT </span><span className="text-gray-400">| </span>
          </div>
          }
          {state.selectedTextType !== plainText &&
          <div className="inline">
            <span><a onClick={() => dispatch({ type: "showPlain", amd: amd })} className="link">TXT</a> </span>
            <span className="text-gray-400">| </span>
          </div>
          }
          <span>
            <Link to={`/pdf/bills/${amd.session}/${amd.printNo}`} target="_blank" className="link">PDF</Link>
          </span>
        </div>
      </div>
      <div className="my-3">
        {state.textEl}
      </div>
    </div>
  )
}
