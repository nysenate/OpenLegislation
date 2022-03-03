import React from "react";
import { Link } from "react-router-dom";
import MemberThumbnail from "app/shared/MemberThumbnail";
import { formatDateTime } from "app/lib/dateUtils";
import { DateTime } from "luxon";
import BillUpdate from "app/shared/BillUpdate";


export default function BillUpdateResults({ billUpdates, showDetail }) {
  if (!billUpdates) {
    return null
  }

  return (
    <div>
      {billUpdates.map((update, index) => {
        return (
          <div key={index}>
            <UpdateResult billUpdate={update} showDetail={showDetail} />
          </div>
        )
      })}
    </div>
  )
}

function UpdateResult({ billUpdate, showDetail }) {
  return (
    <Link to={`/bills/${billUpdate.id.session}/${billUpdate.id.basePrintNo}`}>
      <div className="hover:bg-gray-200 rounded">
        <div className="p-3 flex flex-wrap justify-between items-center">
          <div className="flex items-center w-full lg:w-3/12">
            <MemberThumbnail member={billUpdate?.item?.sponsor?.member} />
            <div>
              <div className="text">
                {billUpdate.id.basePrintNo}-{billUpdate.id.session}
              </div>
              <div className="text text--small">
                {billUpdate.item.sponsor && billUpdate.item.sponsor.member &&
                billUpdate.item.sponsor.member.fullName}
              </div>
            </div>
          </div>

          <div className="w-full lg:w-4/12 my-3 lg:my-0 text text--small">
            {billUpdate.item.title}
          </div>

          <div className="w-full lg:w-4/12 text text--small">
            <div>
              <span className="font-semibold">Last Published:</span> {formatDateTime(billUpdate.sourceDateTime, DateTime.DATETIME_MED)}
            </div>
            <div>
              <span className="font-semibold">Last Processed:</span> {formatDateTime(billUpdate.processedDateTime, DateTime.DATETIME_MED)}
            </div>
            <div><span className="font-semibold">Update Source Id:</span> {billUpdate.sourceId}</div>
          </div>
        </div>
        {showDetail &&
          <div className="ml-3 pb-3 mb-8 text-gray-800">
            <BillUpdate update={billUpdate} linkSource={false} />
          </div>
        }
      </div>
    </Link>
  )
}
