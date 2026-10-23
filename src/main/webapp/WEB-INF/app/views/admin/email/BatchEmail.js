import React from 'react'
import Input from "app/shared/Input";
import Modal from "app/shared/Modal";
import {
  BatchEmailBody,
  previewBatchEmail,
  sendBatchEmail,
} from "app/apis/batchEmailApi";
import ErrorMessage from "app/shared/ErrorMessage";


export default function BatchEmail({ setHeaderText }) {
  const [ testMode, setTestMode ] = React.useState(true)
  const [ breakingChanges, setBreakingChanges ] = React.useState(false)
  const [ newFeatures, setNewFeatures ] = React.useState(false)
  const [ subject, setSubject ] = React.useState("")
  const [ body, setBody ] = React.useState("")
  const [ isConfirmationDialogOpen, setIsConfirmationDialogOpen ] = React.useState(false)
  const [ isPreviewOpen, setIsPreviewOpen ] = React.useState(false)
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const [ successMsg, setSuccessMsg ] = React.useState("")

  React.useEffect(() => {
    setHeaderText("Batch Email")
  }, [])

  const sendEmail = () => {
    setErrorMsg("")
    setSuccessMsg("")

    const groups = []
    if (breakingChanges) {
      groups.push("BREAKING_CHANGES")
    }
    if (newFeatures) {
      groups.push("NEW_FEATURES")
    }
    const requestBody = new BatchEmailBody(groups, subject, body)
    sendBatchEmail(testMode, requestBody)
      .then(() => setSuccessMsg("Successfully sent emails"))
      .catch((err) => setErrorMsg(err.message))
      .finally(() => setIsConfirmationDialogOpen(false))
  }

  const validate = () => {
    setErrorMsg("")
    setSuccessMsg("")
    if (!breakingChanges && !newFeatures) {
      setErrorMsg("You must select at least one group.")
      return
    }
    if (!subject) {
      setErrorMsg("You must enter a subject.")
      return
    }
    if (!body.trim()) {
      setErrorMsg("You must enter a body.")
      return
    }
    setIsConfirmationDialogOpen(true)
  }

  return (
    <div className="p-3">
      <h3 className="h4 mb-6">Send an email to our API users</h3>
      {errorMsg &&
        <div className="my-3">
          <ErrorMessage>{errorMsg}</ErrorMessage>
        </div>
      }
      {successMsg &&
        <div className="my-3">
          <span className="text text-green-700 font-bold">{successMsg}</span>
        </div>
      }

      <Row>
        <RowLabel><span className="font-bold">Test Mode:</span></RowLabel>
        <RowData>
          <label className="flex items-center">
            <input type="checkbox"
                   value={testMode}
                   defaultChecked={testMode}
                   onChange={e => setTestMode(e.target.checked)}
                   className="peer appearance-none rounded-md" />
            <span className="w-10 h-5 mr-2 flex items-center flex-shrink-0 p-1 bg-gray-300 rounded-full duration-300 ease-in-out peer-checked:bg-blue-400 after:w-4 after:h-4 after:bg-gray-100 after:rounded-full after:shadow-md after:duration-300 peer-checked:after:translate-x-4 group-hover:after:translate-x-1"></span>
            {testMode ? <span className="font-bold">ON</span> : <span className="font-bold">OFF</span>}
          </label>
          <p className="mt-1 italic">
            In test mode, the email will only be sent to you.
          </p>
        </RowData>
      </Row>

      <Row>
        <RowLabel>Groups:</RowLabel>
        <RowData>
          <input type="checkbox"
                 defaultChecked={breakingChanges}
                 onChange={(e) => setBreakingChanges(e.target.checked)}
                 className="cursor-pointer"
                 name="breakingChanges"
                 id="breakingChanges" />
          <label htmlFor="breakingChanges" className="label cursor-pointer m-2">Breaking changes to the API</label>
          <br />
          <input type="checkbox"
                 defaultChecked={newFeatures}
                 onChange={(e) => setNewFeatures(e.target.checked)}
                 className="cursor-pointer"
                 name="newFeatures"
                 id="newFeatures" />
          <label htmlFor="newFeatures" className="label cursor-pointer m-2">New features added to the API</label>
        </RowData>
      </Row>

      <Row>
        <RowLabel>Subject:</RowLabel>
        <RowData>
          <Input label="" value={subject} onChange={(e) => setSubject(e.target.value)} className="w-10/12" />
        </RowData>
      </Row>

      <Row>
        <RowLabel>Body:</RowLabel>
        <RowData>
          <textarea className="textarea w-10/12 h-96" value={body} onChange={e => setBody(e.target.value)} />
          <p className="mt-1 italic">
            Write plain text. Leave a blank line between paragraphs; links starting with http:// or https://
            become clickable. The signature and subscription preferences link are added automatically.
          </p>
        </RowData>
      </Row>

      <Row>
        <RowLabel />
        <RowData>
          <button onClick={() => setIsPreviewOpen(true)} className="btn btn--secondary w-36 mr-3">
            Preview Email
          </button>
          <button onClick={() => validate()} className="btn btn--primary w-36">
            Send Email
          </button>
        </RowData>
      </Row>

      <ConfirmationModal isOpen={isConfirmationDialogOpen}
                         setIsOpen={setIsConfirmationDialogOpen}
                         onConfirm={() => sendEmail()}
                         testMode={testMode} />

      <PreviewDialog isOpen={isPreviewOpen}
                     setIsOpen={setIsPreviewOpen}
                     body={body}
      />
    </div>
  )
}

function ConfirmationModal({ isOpen, setIsOpen, onConfirm, testMode }) {
  if (testMode) {
    return (
      <Modal isOpen={isOpen} onDismiss={() => setIsOpen(false)} ariaLabel="Send email confirmation">
        <div>
          <h4 className="h4 mb-3">Send a test email to yourself?</h4>
          <p className="mb-3">You are currently in test mode. This email will only be sent to you.</p>
          <button onClick={() => setIsOpen(false)} className="btn btn--secondary w-28 mr-3">Cancel</button>
          <button onClick={() => onConfirm()} className="btn btn--primary px-3">Send Test Email</button>
        </div>
      </Modal>
    )
  } else {
    return (
      <Modal isOpen={isOpen} onDismiss={() => setIsOpen(false)} ariaLabel="Send email confirmation">
        <div>
          <h4 className="h4 mb-3">Send this email to API users?</h4>
          <p className="mb-3">This email will be sent to all API users who are subscribed to the selected groups.</p>
          <button onClick={() => setIsOpen(false)} className="btn btn--secondary w-28 mr-3">Cancel</button>
          <button onClick={() => onConfirm()} className="btn btn--primary px-3">Email API Users</button>
        </div>
      </Modal>
    )
  }
}

function PreviewDialog({ isOpen, setIsOpen, body }) {
  const [ html, setHtml ] = React.useState("")
  const [ errorMsg, setErrorMsg ] = React.useState("")

  React.useEffect(() => {
    if (!isOpen) {
      return
    }
    // Ignore a response that arrives after the dialog closed, so it can't replace a newer preview.
    let isCurrent = true
    setHtml("")
    setErrorMsg("")
    previewBatchEmail(new BatchEmailBody([], "", body))
      .then((html) => isCurrent && setHtml(html))
      .catch((err) => isCurrent && setErrorMsg(err.message))
    return () => isCurrent = false
  }, [ isOpen, body ])

  return (
    <Modal isOpen={isOpen} onDismiss={() => setIsOpen(false)} ariaLabel="Email Preview">
      {errorMsg && <ErrorMessage>{errorMsg}</ErrorMessage>}
      {/* The sandbox keeps the email's markup and styles isolated from the page.
          Links open in a new tab, since most sites refuse to load inside a frame. */}
      <iframe srcDoc={html.replace("<head>", "<head><base target=\"_blank\">")}
              sandbox="allow-popups allow-popups-to-escape-sandbox"
              title="Email preview"
              className="w-full h-[70vh] bg-white" />
    </Modal>
  )
}

function Row({ children }) {
  return (
    <div className="flex mt-6 items-start">
      {children}
    </div>
  )
}

function RowLabel({ children }) {
  return (
    <div className="w-28 flex-none">
      {children}
    </div>
  )
}

function RowData({ children }) {
  return (
    <div className="flex-1">
      {children}
    </div>
  )
}
