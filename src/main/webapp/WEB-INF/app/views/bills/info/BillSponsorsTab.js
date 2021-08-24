import React from "react";
import MemberListing from "app/shared/MemberListing";
import {
  CaretDown,
  CaretUp
} from "phosphor-react";


// TODO WIP


function AmendmentSponsors({ bill, selectedAmd }) {
  console.log(bill)
  return (
    <React.Fragment>
      <AmendmentCoSponsors amendment={bill.amendments.items[selectedAmd]} />
    </React.Fragment>
  )
}

function AmendmentCoSponsors({ amendment }) {
  if (amendment.coSponsors.size === 0) {
    return null
  }

  const coSponsorComponents = amendment.coSponsors.items.map((coSponsor) => {
    return (
      <MemberListing member={coSponsor} />
    )
  })

  return (
    <section className="mt-3">
      <header>
        <h3 className="h4 mb-2">Co Sponsors</h3>
      </header>
      <TruncatedList components={coSponsorComponents} />
    </section>
  )
}

function TruncatedList({ components }) {
  const [ isExpanded, setIsExpanded ] = React.useState(false)
  const [ displayedComponents, setDisplayedComponents ] = React.useState([])

  React.useEffect(() => {
    if (isExpanded) {
      setDisplayedComponents(components)
    }
    else {
      setDisplayedComponents(components.slice(0, 3))
    }
  }, [isExpanded])

  return (
    <div className="px-5">
      {displayedComponents.map((d, index) =>
        <div className="py-1" key={index}>
          {d}
        </div>
      )}
      {!isExpanded &&
      <div className="pt-1 flex items-center" onClick={() => setIsExpanded(true)}>
        <CaretDown size="1.25rem" weight="bold" className="text-blue-500 mr-1" />
        <a>See {components.length - 3} more</a>
      </div>
      }
      {isExpanded &&
      <div className="pt-1 flex items-center" onClick={() => setIsExpanded(false)}>
        <CaretUp size="1.25rem" weight="bold" className="text-blue-500 mr-1" />
        <a>Show less</a>
      </div>
      }
    </div>
  )
}

// TODO Implement
function AmendmentCoPrimeSponsors({ bill, selectedAmd }) {
  if (bill.additionalSponsors.size === 0) {
    return null
  }

  return (
    ""
  )
}
