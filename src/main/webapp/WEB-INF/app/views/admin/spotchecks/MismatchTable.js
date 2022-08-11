import * as textUtils from "app/lib/textUtils";
import * as dateUtils from "app/lib/dateUtils";
import { DateTime } from "luxon";
import React from "react";
import Modal from "app/shared/Modal";
import Mismatch from "app/views/admin/spotchecks/Mismatch";
import { ignoreMismatchApi } from "app/apis/spotcheckApi";


export default function MismatchTable({
                                        mismatches,
                                        contentType,
                                        mismatchTypeToDisplayName,
                                        referenceTypesByName,
                                        reloadMismatches
                                      }) {
  const [ isModalOpen, setIsModalOpen ] = React.useState(false)
  const [ modalMismatchId, setModalMismatchId ] = React.useState()

  const openModal = mismatchId => {
    setModalMismatchId(mismatchId)
    setIsModalOpen(true)
  }

  const ignoreMismatch = mismatch => {
    ignoreMismatchApi(mismatch.mismatchId)
      .then(() => reloadMismatches())
  }

  return (
    <div>
      <table className="table">
        <thead>
        <tr>
          <th>State</th>
          {getContentColumns(contentType)}
          <th>Error</th>
          <th>Date</th>
          <th>Source</th>
          <th></th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        {mismatches.map(m => {
          return (
            <tr key={m.mismatchId}>
              <td>{textUtils.capitalize(m.status)}</td>
              {getContentValues(contentType, m)}
              <td>{mismatchTypeToDisplayName.get(m.mismatchType)}</td>
              <td>{dateUtils.formatDateTime(m.observedDateTime, DateTime.DATETIME_SHORT)}</td>
              <td>{referenceTypesByName.get(m.referenceType).displayName}</td>
              <td>
                <button className="btn btn--primary my-3 w-20" onClick={() => openModal(m.mismatchId)}>Diff</button>
              </td>
              <td>
                <button className="btn btn--secondary my-3 w-20" onClick={() => ignoreMismatch(m)}>Ignore</button>
              </td>
            </tr>
          )
        })}
        </tbody>
      </table>

      <Modal isOpen={isModalOpen}
             onDismiss={() => setIsModalOpen(false)}
             className="spotcheck-diff-modal"
             ariaLabel="mismatch diff">
        <Mismatch mismatchId={modalMismatchId} mismatchTypeToDisplayName={mismatchTypeToDisplayName} />
      </Modal>
    </div>
  )
}


const getContentColumns = contentType => {
  switch (contentType) {
    case "BILL":
    case "BILL_AMENDMENT":
      return <>
        <th>Session</th>
        <th>Print No.</th>
      </>
    case "CALENDAR":
      return <>
        <th>Year</th>
        <th>Cal No.</th>
        <th>Type</th>
      </>
    case "AGENDA":
      return <>
        <th>Year</th>
        <th>Agenda No.</th>
        <th>Committee</th>
      </>
    case "AGENDA_WEEK":
      return <>
        <th>Week of</th>
        <th>Committee</th>
      </>
    case "LAW":
      return <>
        <th>Chapter</th>
        <th>Loc Id</th>
      </>
  }
}

const getContentValues = (contentType, mismatch) => {
  switch (contentType) {
    case "BILL":
    case "BILL_AMENDMENT":
      return <>
        <td>{mismatch?.key?.session?.year}</td>
        <td>{mismatch?.key?.printNo}</td>
      </>
    case "CALENDAR":
      return <>
        <td>{mismatch?.key?.year}</td>
        <td>{mismatch?.key?.calNo}</td>
        <td>{mismatch?.key?.type}</td>
      </>
    case "AGENDA":
      return <>
        <td>{mismatch?.key?.agendaId?.year}</td>
        <td>{mismatch?.key?.agendaId?.number}</td>
        <td>{mismatch?.key?.committeeId?.name}</td>
      </>
    case "AGENDA_WEEK":
      return <>
        <td>{mismatch?.key?.weekOf}</td>
        <td>{mismatch?.key?.committeeId?.name}</td>
      </>
    case "LAW":
      if (mismatch?.mismatchType === "LAW_IDS") {
        return <>
          <td>All</td>
          <td></td>
        </>
      }
      return <>
        <td>{mismatch?.key?.lawChapter}</td>
        <td>{mismatch?.key?.locationId}</td>
      </>
  }
}
