import React from "react"
import { Link } from "react-router-dom";

export default function BillJsonTab({ bill }) {
  return (
    <section className="m-5">
      <div className="mb-5">
        <Link to={`/api/3/bills/${bill.session}/${bill.printNo}`}
              target="_blank" className="link">
          Open JSON in new window
        </Link>
      </div>
      <pre className="text text--small whitespace-pre-wrap">
        {JSON.stringify(bill, null, 2)}
      </pre>
    </section>
  )
}