import React from "react"
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";
import { capitalize } from "app/lib/textUtils";
import { CalendarBlank } from "phosphor-react";
import Accordion from "app/shared/Accordion";
import MemberListing from "app/shared/MemberListing";

export default function BillVotesTab({ bill, selectedAmd }) {
  if (!bill) {
    return null
  }
  return (
    <section className="m-5">
      {bill.votes.items.map((vote) => {
        return (
          <div key={vote.voteDate + vote.voteType} className="mb-14">
            <div className="flex">
              <VoteInfo vote={vote} />
              <VoteSummaryTable vote={vote} />
            </div>
            <VoteDetails vote={vote} />
          </div>)
      })}
    </section>
  )
}

function VoteInfo({ vote }) {
  return (
    <div className="w-6/12">
      <div>
        <CalendarBlank color="#374151" size="1rem" weight="bold" className="inline mr-1" />
        <span className="font-semibold">{formatDateTime(vote.voteDate, DateTime.DATE_FULL)}</span>
      </div>
      <br />
      <div>
        <span className="font-semibold">{vote?.committee?.name} {capitalize(vote.voteType)} Vote</span>
      </div>
      <div>
        Voted on Amendment revision: {vote.version === "" ? "Original" : vote.version}
      </div>
    </div>
  )
}

function VoteSummaryTable({ vote }) {
  return (
    <div className="w-6/12 flex flex-wrap content-start">
      <div className="w-6/12 font-semibold border-b-1 border-gray-400">
        Vote
      </div>
      <div className="w-6/12 font-semibold border-b-1 border-gray-400">
        Count
      </div>
      {Object.entries(vote.memberVotes.items).map(([ key, memberVotes ], index) => {
        let className = "w-6/12 pl-1 " + bgColorClass(key)
        return (
          <React.Fragment key={key}>
            <div className={className}>
              {voteTypeLabel(key)}
            </div>
            <div className={className}>
              {memberVotes.size}
            </div>
          </React.Fragment>)
      })}
    </div>
  )
}

function VoteDetails({ vote }) {
  return (
    <div className="mt-2">
      <Accordion title="Voting Details">
        <div className="">
          {Object.entries(vote.memberVotes.items).map(([ key, memberVotes ], index) => {
            return vote.memberVotes.items[key].items.map((member) => {
              return (
                <div className={`flex items-center content-start p-2 ${bgColorClass(key)}`} key={member.memberId}>
                  <div className="w-24">{voteTypeLabel(key)}</div>
                  <MemberListing member={member} />
                </div>
              )
            })
          })}
        </div>
      </Accordion>
    </div>
  )
}

const voteTypeLabel = (voteType) => {
  return voteType === "AYEWR" ? "Aye with reservations" : capitalize(voteType)
}

const bgColorClass = (voteType) => {
  switch (voteType) {
    case "AYE":
    case "AYEWR":
      return " bg-green-50"
    case "NAY":
      return " bg-red-50"
  }
}
