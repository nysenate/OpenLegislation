import React from 'react'
import Pagination from "app/shared/Pagination";
import { Link } from "react-router-dom";
import MemberThumbnail from "app/shared/MemberThumbnail";
import FullDate from "app/shared/FullDate";
import BillStatusDesc from "app/shared/BillStatusDesc";
import BillMilestones from "app/shared/BillMilestones";

export default function SearchResults({ response, limit, page, onPageChange }) {

  if (response.result.items.length === 0) {
    return (
      <div>
        No results found
      </div>
    )
  }

  return (
    <div className="mt-8">
      <div className="flex justify-center">
        <span className="font-semibold">{response.total.toLocaleString()}</span>&nbsp;matching bills were found.
      </div>
      <div className="pt-3">
        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.total}
        />
        <ResultList results={response.result.items} />
        <Pagination
          limit={limit}
          currentPage={page}
          onPageChange={onPageChange}
          total={response.total}
        />
      </div>
    </div>
  )
}

function ResultList({ results }) {
  return (
    <div>
      {results.map((r) =>
        <Link to={`/bills/${r.result.session}/${r.result.basePrintNo}`}
              key={r.result.basePrintNoStr}>
          <ResultItem result={r} />
        </Link>
      )}
    </div>
  )
}

function ResultItem({ result }) {
  const bill = result.result
  return (
    <div className="p-3 hover:bg-gray-200 flex flex-wrap">
      <div className="flex items-center w-full md:w-1/3">
        <MemberThumbnail member={bill.sponsor.member} />
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
          {result.highlights.title
            ? <span className="highlight" dangerouslySetInnerHTML={{ __html: result.highlights.title }} />
            : <span>{bill.title}</span>
          }
        </div>
        {bill.status.actionDate &&
        <div className="mt-1 text text-blue-600">
          <FullDate date={bill.status.actionDate} /> - <BillStatusDesc status={bill.status} />
        </div>
        }
        <BillMilestones milestones={bill.milestones.items}
                        chamber={bill.billType.chamber}
                        className="py-3" />
      </div>
    </div>
  )
}