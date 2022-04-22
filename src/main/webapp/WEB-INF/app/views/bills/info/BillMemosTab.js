import React from 'react'
import { Link } from "react-router-dom";

export default function BillMemosTab({ bill, selectedAmd }) {
  return (
    <section className="m-5">
      <header>
        <h3 className="h5">Sponsor's Memorandum</h3>
      </header>
      <div className="mx-5">
        <pre className="text text--small">
          {bill.amendments.items[selectedAmd].memo}
        </pre>
        {bill.billType.chamber === "ASSEMBLY" &&
        <AssemblyMemoNote bill={bill} selectedAmd={selectedAmd} />
        }
      </div>
    </section>
  )
}

function AssemblyMemoNote({ bill, selectedAmd }) {
  const sameAsBill = bill.amendments.items[selectedAmd].sameAs?.items[0]
  return (
    <div className="mt-3">
      <p>Sponsor memos are not provided for Assembly Bills.</p>
      {sameAsBill &&
      <p>You can view the sponsor memo for the Senate version of this bill here:&nbsp;
        <Link to={`/bills/${sameAsBill.session}/${sameAsBill.basePrintNo}?tab=Memos`} className="link">{sameAsBill.basePrintNo}</Link>
      </p>
      }
    </div>
  )
}
