import React, { useEffect } from 'react';
import { getBillApi } from "app/apis/billGetApi";
import { sessionYear } from "app/lib/dateUtils";
import { Link } from "react-router-dom";

export default function Bill({ setHeaderText, match }) {

  const [ bill, setBill ] = React.useState({})
  const [ loading, setLoading ] = React.useState(true)

  useEffect(() => {
    getBillApi(match.params.sessionYear, match.params.printNo, "with_refs_no_fulltext")
      .then((bill) => {
        setBill(bill)
        setLoading(false)
      })
  }, [])

  useEffect(() => {
    if (bill && bill.billType) {
      setHeaderText(headerTextForBill(bill))
    }
  }, [ bill ])

  function headerTextForBill(bill) {
    const type = bill.billType.resolution ? "Resolution" : "Bill"
    const isActiveText = bill.session === sessionYear() ? "" : "(Inactive)"
    return `NYS ${bill.billType.desc} ${type} ${bill.printNo}-${bill.session} ${isActiveText}`
  }

  if (loading) {
    return (<div>Loading ...</div>)
  }

  return (
    <div className="p-5">
      <SubstitutedByMsg bill={bill} />
      <h3 className="h3 px-3 pb-5">{bill.title}</h3>
      {/* TODO add programInfo section.. see bill-view.js:16:20 */}
      <Summary bill={bill} />
    </div>
  )
}

function SubstitutedByMsg({ bill }) {
  // TODO Test and prettify the substitution message.
  if (bill.substitutedBy) {
    return (
      <div>
        <p>
          This bill has been substituted by
          <Link to={`/bills/${bill.substitutedBy.session}/${bill.substitutedBy.basePrintNo}`}>
            {bill.substitutedBy.basePrintNo} - {bill.substitutedBy.session}
          </Link>
        </p>
      </div>
    )
  }
  return null
}

function Summary({ bill }) {
  const sponsorEls = bill.sponsor.budget ? (
      <div>
        <h4 className="h4">Budget Bill</h4>
      </div>
    )
    : (
      <div className="flex flex-col">
        <span className="text">Sponsored By</span>
        <span className="text text--large">{bill.sponsor.member.fullName}</span>
        <span className="text text--small">District {bill.sponsor.member.districtCode}</span>
      </div>
    )

  return (
    <div className="flex">
      <img className="h-24 mr-3" src={`/static/img/business_assets/members/mini/${bill.sponsor.member.imgName}`} />
      {sponsorEls}

      <div>

      </div>
    </div>
  )
}