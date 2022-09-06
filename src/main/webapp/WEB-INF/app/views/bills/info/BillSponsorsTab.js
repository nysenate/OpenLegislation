import React from "react";
import MemberListing from "app/shared/MemberListing";
import TruncatedList from "app/shared/TruncatedList";


export default function BillSponsorsTab({ bill, selectedAmd }) {
  return (
    <div className="mx-5">
      <div>
        <CoPrimeList bill={bill} />
      </div>
      <div>
        <CoSponsorsList amendment={bill.amendments.items[selectedAmd]} />
      </div>
      <div>
        <MultiSponsorList amendment={bill.amendments.items[selectedAmd]} />
      </div>
    </div>
  )
}

function CoPrimeList({ bill }) {
  if (bill.additionalSponsors.size === 0) {
    return null
  }

  const coPrimeComponents = bill.additionalSponsors.items.map((coPrime) => <MemberListing member={coPrime} />)

  return (
    <section className="mt-5">
      <header>
        <h3 className="h5 mb-1">Co Prime Sponsors</h3>
      </header>
      <TruncatedList list={coPrimeComponents} />
    </section>
  )
}

function CoSponsorsList({ amendment }) {
  if (amendment.coSponsors.size === 0) {
    return null
  }

  const coSponsorComponents = amendment.coSponsors.items.map((coSponsor) => {
    return (
      <MemberListing member={coSponsor} />
    )
  })

  return (
    <section className="mt-5">
      <header>
        <h3 className="h5 mb-1">Co Sponsors</h3>
      </header>
      <TruncatedList list={coSponsorComponents} />
    </section>
  )
}

function MultiSponsorList({ amendment }) {
  if (amendment.multiSponsors.size === 0) {
    return null
  }

  const multiSponsorEls = amendment.multiSponsors.items.map((multiSponsor) => <MemberListing member={multiSponsor} />)

  return (
    <section className="mt-5">
      <header>
        <h3 className="h5 mb-1">Multi Sponsors</h3>
      </header>
      <TruncatedList list={multiSponsorEls} />
    </section>
  )
}
