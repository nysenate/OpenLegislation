import React from 'react'
import MemberThumbnail from "app/shared/MemberThumbnail";
import { DateTime } from "luxon";
import {
  CheckCircle,
  XCircle
} from "phosphor-react";
import BillStatusDesc from "app/shared/BillStatusDesc";
import BillMilestones from "app/shared/BillMilestones";
import { formatDateTime } from "app/lib/dateUtils";

export default function BillOverview({ bill }) {
  return (
    <div className="flex justify-start flex-wrap">
      <div className="flex items-center pr-8">
        <MemberThumbnail member={bill.sponsor?.member} />
        <SponsorInfo sponsor={bill.sponsor} />
      </div>
      {billStatusMessages(bill).map((s) =>
        <div className="my-3 mr-8" key={s.printNo}>
          <BillStatus bill={s} />
        </div>
      )}
    </div>
  )
}

function billStatusMessages(bill) {
  let statuses = [ bill ]
  const sameAsBill = bill?.billInfoRefs?.items?.[bill?.substitutedBy?.basePrintNoStr]
  if (sameAsBill) {
    statuses.push(sameAsBill)
  }
  return statuses;
}

function SponsorInfo({ sponsor }) {
  if (!sponsor) {
    return null
  }
  else if (sponsor.budget) {
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

function BillStatus({ bill }) {
  return (
    <React.Fragment>
      <div className="text text--small">
        Status as of {formatDateTime(bill.status.actionDate, DateTime.DATE_FULL)}&nbsp;
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