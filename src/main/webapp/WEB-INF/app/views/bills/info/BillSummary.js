import React from 'react'
import MemberThumbnail from "app/shared/MemberThumbnail";
import {
  CheckCircle,
  FileDotted,
  XCircle
} from "phosphor-react";
import { Link } from "react-router-dom";
import Date from "app/shared/Date";
import BillStatusDesc from "app/shared/BillStatusDesc";
import BillMilestones from "app/shared/BillMilestones";
import { DateTime } from "luxon";


export default function BillSummary({ bill }) {
  return (
    <div className="px-5 pt-5">
      <SubstitutedByMsg bill={bill} />
      <h3 className="h3">{bill.title}</h3>
      <ProgramInfoMsg bill={bill} />

      <div className="flex justify-start flex-wrap w-11/12 mt-2">
        <div className="flex my-3 mr-10">
          <MemberThumbnail member={bill.sponsor.member} />
          <SponsorInfo sponsor={bill.sponsor} />
        </div>
        {billStatusMessages(bill).map((s) =>
          <div className="my-3 mr-10" key={s.printNo}>
            <BillStatus bill={s} />
          </div>
        )}
      </div>
    </div>
  )
}


function SubstitutedByMsg({ bill }) {
  if (!bill.substitutedBy) {
    return null
  }
  return (
    <div className="bg-yellow-50 py-2 mb-2 flex items-center rounded">
      <FileDotted size="1.2rem" className="mx-1" />
      <p>
        This bill has been substituted by&nbsp;
        <Link to={`/bills/${bill.substitutedBy.session}/${bill.substitutedBy.basePrintNo}`}>
          {bill.substitutedBy.basePrintNo} - {bill.substitutedBy.session}
        </Link>
      </p>
    </div>
  )
}

function ProgramInfoMsg({ bill }) {
  if (!bill.programInfo) {
    return null
  }
  return (
    <div className="pb-3 text">
      Bill #{bill.programInfo.sequenceNo} on the program
      for <span className="font-medium">{bill.programInfo.name}</span>
    </div>
  )
}

function SponsorInfo({ sponsor }) {
  if (sponsor.budget) {
    return (
      <div>
        <h4 className="h4">Budget Bill</h4>
      </div>
    )
  } else if (sponsor.rules) {
    return (
      <div>
        <h4 className="h4">From the Rules Committee</h4>
      </div>
    )
  } else {
    return (
      <div className="flex flex-col">
        <span className="text">Sponsored By</span>
        <span className="text text--large">{sponsor.member.fullName}</span>
        <span className="text text--small">District {sponsor.member.districtCode}</span>
      </div>
    )
  }
}

function billStatusMessages(bill) {
  let statuses = [ bill ]
  const sameAsBill = bill?.billInfoRefs?.items?.[bill?.substitutedBy?.basePrintNoStr]
  if (sameAsBill) {
    statuses.push(sameAsBill)
  }
  return statuses;
}

function BillStatus({ bill }) {
  return (
    <React.Fragment>
      <div className="text text--small">
        Status as of <Date date={bill.status.actionDate} format={DateTime.DATE_FULL} />&nbsp;
        <span className="font-semibold">({bill.basePrintNo})</span>
      </div>
      <div className="flex items-center">
        {(bill.signed || bill.adopted) &&
        <CheckCircle size="1.4rem" weight="bold" className="inline text-blue-500 mr-0.5" />
        }
        {bill.vetoed &&
        <XCircle size="1.4rem" weight="bold" className="inline text-red-500 mr-0.5" />
        }
        <span className="text font-semibold"><BillStatusDesc status={bill.status} /></span>
      </div>
      <div>
        {!bill.billType.resolution &&
        <BillMilestones milestones={bill.milestones.items}
                        chamber={bill.billType.chamber}
                        className="py-3" />
        }
      </div>
    </React.Fragment>
  )
}