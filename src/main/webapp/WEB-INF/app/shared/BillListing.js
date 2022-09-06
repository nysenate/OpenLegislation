import React from 'react'
import MemberThumbnail from "app/shared/MemberThumbnail";
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import BillStatusDesc from "app/shared/BillStatusDesc";
import BillMilestones from "app/shared/BillMilestones";
import { Link } from "react-router-dom";

/**
 * Displays a bill's summary information.
 * @param bill The bill
 * @param to Go to the 'to' page when this listing is clicked. If navigation is not desired, leave this undefined.
 * @param highlights Text with '<EM>' highlights from Elasticsearch search results. These results are displayed
 * in place of the title if given. Leave undefined if you don't have/want search results.
 * @constructor
 */
export default function BillListing({ bill, to, highlights }) {
  if (!bill) {
    return null
  }
  if (!to) {
    return <BillListingDetail bill={bill} highlights={highlights} />
  }

  return <LinkedBillListing bill={bill} to={to} highlights={highlights} />
}

function LinkedBillListing({ bill, to, highlights }) {
  return (
    <Link to={to}>
      <div className="hover:bg-gray-200 rounded">
        <BillListingDetail bill={bill} highlights={highlights} />
      </div>
    </Link>
  )
}

function BillListingDetail({ bill, highlights }) {
  return (
    <div className="p-3 flex flex-wrap">
      <div className="flex items-center w-full md:w-1/3">
        <MemberThumbnail member={bill?.sponsor?.member} />
        <div>
          <div className="text">
            {bill.basePrintNo}-{bill.session}
          </div>
          <div className="text text--small">
            {bill.sponsor && bill.sponsor.member &&
              bill.sponsor.member.fullName}
          </div>
        </div>
      </div>

      <div className="w-full md:w-2/3 mt-2 md:mt-0">
        <div className="text">
          {highlights
            ? <span className="highlight" dangerouslySetInnerHTML={{ __html: highlights }} />
            : <span>{bill.title}</span>
          }
        </div>
        {bill.status.actionDate &&
          <div className="mt-1 text text-blue-600">
            {formatDateTime(bill.status.actionDate, DateTime.DATE_FULL)} - <BillStatusDesc status={bill.status} />
          </div>
        }
        {!bill.billType.resolution &&
          <BillMilestones milestones={bill.milestones.items}
                          chamber={bill.billType.chamber}
                          className="py-3" />
        }
      </div>
    </div>
  )
}

/**
 * A much simpler bill listing which only displays the print number and session year.
 * Useful for when a bill is referenced that does not exist in our database (i.e. pre 2009 bills).
 */
export function BillInfoListing({ billInfo }) {
  return (
    <div className="p-3 flex flex-wrap">
      <div className="text">
        {billInfo.basePrintNo} - {billInfo.session}
      </div>
    </div>
  )
}
