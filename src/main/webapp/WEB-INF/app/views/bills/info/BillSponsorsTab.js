import React from "react";
import MemberListing from "app/shared/MemberListing";
import {
  CaretDown,
  CaretUp
} from "phosphor-react";


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
      <TruncatedList components={coPrimeComponents} />
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
      <TruncatedList components={coSponsorComponents} />
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
      <TruncatedList components={multiSponsorEls} />
    </section>
  )
}

function TruncatedList({ components }) {
  const displaySize = 3
  const requiresTruncation = components.length > displaySize
  const [ isExpanded, setIsExpanded ] = React.useState(false)
  const [ displayedComponents, setDisplayedComponents ] = React.useState([])

  React.useEffect(() => {
    if (isExpanded) {
      setDisplayedComponents(components)
    } else {
      setDisplayedComponents(components.slice(0, displaySize))
    }
  }, [ isExpanded ])

  return (
    <div className="px-5">
      {displayedComponents.map((el, index) =>
        <div className="py-1" key={index}>
          {el}
        </div>
      )}
      {!isExpanded && requiresTruncation &&
      <div className="pt-1 flex items-center" onClick={() => setIsExpanded(true)}>
        <CaretDown size="1.25rem" weight="bold" className="text-blue-500 mr-1" />
        <a>Show {components.length - displaySize} more</a>
      </div>
      }
      {isExpanded && requiresTruncation &&
      <div className="pt-1 flex items-center" onClick={() => setIsExpanded(false)}>
        <CaretUp size="1.25rem" weight="bold" className="text-blue-500 mr-1" />
        <a>Show less</a>
      </div>
      }
    </div>
  )
}
