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
  const [ result, setResult ] = React.useState(null)
  const [ isSending, setIsSending ] = React.useState(false)
  const sending = React.useRef(false)

  React.useEffect(() => {
    setHeaderText("Batch Email")
  }, [])

  // The last report stays until another send completes, so a failed request doesn't lose the list
  // of recipients to follow up with.
  const sendEmail = () => {
    // A batch can take minutes; a second click must not send it again. The ref also covers clicks
    // that arrive before the disabled buttons are rendered.
    if (sending.current) {
      return
    }
    sending.current = true
    setIsSending(true)
    setErrorMsg("")

    const groups = []
    if (breakingChanges) {
      groups.push("BREAKING_CHANGES")
    }
    if (newFeatures) {
      groups.push("NEW_FEATURES")
    }
    const requestBody = new BatchEmailBody(groups, subject, body)
    sendBatchEmail(testMode, requestBody)
      .then((data) => setResult({ ...data.result, message: data.message, testMode }))
      .catch((err) => setErrorMsg(err.errorCode === undefined
        // No reply from the server, such as a proxy timeout or a dropped connection: it may still be sending.
        ? `${err.message}. The server may still be sending this email; check the server log for the batch `
          + "totals before sending again, or recipients may get it twice."
        : err.message))
      .finally(() => {
        sending.current = false
        setIsSending(false)
        setIsConfirmationDialogOpen(false)
      })
  }

  const validate = () => {
    setErrorMsg("")
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
      {result &&
        <div className="my-3">
          {/* Shown beside a new error, the report must not read as the outcome of the new attempt. */}
          {(errorMsg || isSending) &&
            <p className="mb-1 italic">Previous send ({result.testMode ? "test email" : "to API users"}):</p>
          }
          <SendResult result={result} />
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
          <button onClick={() => validate()} disabled={isSending} className="btn btn--primary w-36">
            {isSending ? "Sending…" : "Send Email"}
          </button>
        </RowData>
      </Row>

      <ConfirmationModal isOpen={isConfirmationDialogOpen}
                         setIsOpen={setIsConfirmationDialogOpen}
                         onConfirm={() => sendEmail()}
                         isSending={isSending}
                         testMode={testMode} />

      <PreviewDialog isOpen={isPreviewOpen}
                     setIsOpen={setIsPreviewOpen}
                     body={body}
      />
    </div>
  )
}

function ConfirmationModal({ isOpen, setIsOpen, onConfirm, isSending, testMode }) {
  if (testMode) {
    return (
      <Modal isOpen={isOpen} onDismiss={() => setIsOpen(false)} ariaLabel="Send email confirmation">
        <div>
          <h4 className="h4 mb-3">Send a test email to yourself?</h4>
          <p className="mb-3">You are currently in test mode. This email will only be sent to you.</p>
          <button onClick={() => setIsOpen(false)} className="btn btn--secondary w-28 mr-3">Cancel</button>
          <button onClick={() => onConfirm()} disabled={isSending} className="btn btn--primary px-3">
            {isSending ? "Sending…" : "Send Test Email"}
          </button>
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
          <button onClick={() => onConfirm()} disabled={isSending} className="btn btn--primary px-3">
            {isSending ? "Sending…" : "Email API Users"}
          </button>
        </div>
      </Modal>
    )
  }
}

const UNSENT_TITLES = {
  SKIPPED: "Skipped: the stored address can't be used",
  FAILED: "Failed: not delivered",
  UNCONFIRMED: "Unconfirmed: the mail server didn't confirm it, so it may have been delivered",
  NOT_SENT: "Not sent: the mail server couldn't be reached, rejected the login or stopped replying",
}

function SendResult({ result }) {
  const unsent = result.unsent
  if (unsent.length === 0) {
    return <span className="text text-green-700 font-bold">{result.message}.</span>
  }
  const withStatus = (status) => unsent.filter((recipient) => recipient.status === status)
  const unconfirmed = withStatus("UNCONFIRMED").length
  const duplicates = [
    result.sent > 0 && `the ${plural(result.sent, "recipient")} who got it`,
    unconfirmed > 0 && `possibly the ${unconfirmed} unconfirmed`,
  ].filter(Boolean)
  return (
    <div>
      <ErrorMessage>
        {result.message}. {plural(unsent.length, "recipient")} may not
        have got this email, and nothing is retried automatically.
        {duplicates.length > 0 && ` Sending again would also send it again to ${duplicates.join(" and ")}.`}
      </ErrorMessage>
      {Object.entries(UNSENT_TITLES).map(([status, title]) =>
        <UnsentList key={status} title={title} recipients={withStatus(status)} />
      )}
    </div>
  )
}

function UnsentList({ title, recipients }) {
  if (recipients.length === 0) {
    return null
  }
  return (
    <div className="mt-3">
      <h4 className="font-bold">{title} ({recipients.length})</h4>
      <ul className="list-disc ml-6">
        {recipients.map((recipient, i) =>
          <li key={i}>
            <span className="font-mono">{recipient.email ?? "(no address)"}</span>: {recipient.reason}
          </li>
        )}
      </ul>
    </div>
  )
}

function plural(count, noun) {
  return `${count} ${noun}${count === 1 ? "" : "s"}`
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
