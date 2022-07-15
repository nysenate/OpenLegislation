import React from "react"
import Modal from "app/shared/Modal";
import Mismatch from "app/views/admin/spotchecks/Mismatch";
import { fetchMismatchTypes } from "app/apis/spotcheckApi";


export default function SpotcheckReports({ setHeaderText }) {
  const [ mismatchTypeToDisplayName, setMismatchTypeToDisplayName ] = React.useState()
  const [ isLoading, setIsLoading ] = React.useState(true)

  React.useEffect(() => {
    fetchMismatchTypes()
      .then(res => setMismatchTypeToDisplayName(new Map(res.result.items.map(mt => [ mt.name, mt.displayName ]))))
      .finally(() => setIsLoading(false))

    setHeaderText("Spotcheck Reports")
  }, [])

  return (
    <div>
      Spotchecks!
      {!isLoading &&
        <Modal isOpen={true} className="spotcheck-diff-modal" ariaLabel="mismatch diff">
          <Mismatch mismatchId={1} mismatchTypeToDisplayName={mismatchTypeToDisplayName} />
        </Modal>
      }
    </div>
  )
}
