import React from 'react'
import { Link } from "react-router-dom";
import BillListingQueried from "app/shared/BillListingQueried";


export default function BillSummaryTab({ bill, selectedAmd }) {
  return (
    <div className="m-5">
      <SameAs bill={bill} selectedAmd={selectedAmd} />
      <EnactingClause bill={bill} selectedAmd={selectedAmd} />
      <Summary bill={bill} />
      <AffectedLaw amendment={bill.amendments.items[selectedAmd]} billStatus={bill.status} />
      <AgendaCalendarReferences bill={bill} />
      <TranscriptReferences amendment={bill.amendments.items[selectedAmd]} />
      <PreviousVersions bill={bill} />
    </div>
  )
}

function SameAs({ bill, selectedAmd }) {
  if (bill.amendments.items[selectedAmd].sameAs.size === 0) {
    return null;
  }
  return (
    <section>
      <header>
        <h3 className="h5">Same As Bills</h3>
      </header>
      <div className="mx-2">
        {bill.amendments.items[selectedAmd].sameAs.items.map((sameAs) => {
            return <BillListingQueried billInfo={sameAs} key={sameAs.basePrintNo} />
          }
        )}
      </div>
    </section>
  )
}

function EnactingClause({ bill, selectedAmd }) {
  if (bill.billType.resolution) {
    return null
  }

  return (
    <section className="mt-5">
      <header>
        <h3 className="h5">Enacting Clause</h3>
      </header>
      <div className="mx-5 my-3">
        <p className="text whitespace-pre-wrap">
          {bill.amendments.items[selectedAmd].actClause || "Not Available"}
        </p>
      </div>

    </section>
  )
}

function Summary({ bill }) {
  if (bill.billType.resolution) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Summary of Bill</h3>
      </header>
      <div className="mx-5 my-3">
        <p className="text whitespace-pre-wrap">
          {bill.summary || "Not Available"}
        </p>
      </div>
    </section>
  )
}

function AffectedLaw({ amendment, billStatus }) {
  // TODO: determine whether bill is passed and find date of original and new law
  // TODO: should substituted bills have the same links
  const isPassed = billStatus.statusType === 'ADOPTED' || billStatus.statusType === 'SIGNED_BY_GOV' || billStatus.statusType === 'POCKET_APPROVAL'
  const oldDate = new Date(billStatus.actionDate)
  oldDate.setDate(oldDate.getDate() - 21)
  const newDate = new Date(billStatus.actionDate)
  newDate.setDate(newDate.getDate() + 21)
  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Affected Law</h3>
      </header>
      <div className="mx-5 my-3">
        {amendment.lawSection === "Resolutions, Legislative" ?
          <p className="text">Primary Law Section - {amendment.lawSection}</p> :
          <Link to={lawSectionToHref(amendment.lawSection)} target="_blank" className="link">Primary Law Section - {amendment.lawSection}</Link>}
      </div>
      {amendment.relatedLaws.size !== 0 &&
      <div className="mx-5 my-3">
        <p className="text">Related Laws:</p>
        {Object.entries(amendment.relatedLaws.items).map(([ key, value ]) => {
          return (
            <p key={key}>
              {key}&nbsp;
              {value.items.map((law, index) => {
                return (
                  <React.Fragment key={law}>
                    {index !== 0 &&
                    <span>,&nbsp;</span>
                    }
                    {key === 'AMEND' && isPassed &&
                      <span>
                        {law}(<Link to={lawToHref(law, oldDate)} target="_blank" className="link">old</Link>,&nbsp;
                        <Link to={lawToHref(law, newDate)} target="_blank" className="link">new</Link>)
                      </span>
                    }
                    {key === 'AMEND' && !isPassed &&
                      <Link to={lawToHref(law, oldDate)} target="_blank" className="link">{law}</Link>
                    }
                    {key === 'ADD' && isPassed &&
                      <Link to={lawToHref(law, newDate)} target="_blank" className="link">{law}</Link>
                    }
                    {key === 'REPEAL' &&
                      <Link to={lawToHref(law, oldDate)} target="_blank" className="link">{law}</Link>
                    }
                    {((key === 'ADD' && !isPassed) || !(key === 'ADD' || key === 'REPEAL' || key === 'AMEND')) &&
                      <span>{law}</span>
                    }
                  </React.Fragment>
                )
              })}
            </p>
          )
        })}
      </div>
      }
    </section>
  )
}

const LAW_SECTIONS = {
  'Abandoned Property': 'ABP',
  'Agriculture & Markets': 'AGM',
  'Alcoholic Beverage Control': 'ABC',
  'Alternative County Government': 'ACG',
  'Arts and Cultural Affairs': 'ACA',
  'Banking': 'BNK',
  'Benevolent Orders': 'BVO',
  'Business Corporation': 'BSC',
  'Canal': 'CAL',
  'Cannabis': 'CAN',
  'Civil Practice Law & Rules': 'CVP',
  'Civil Rights': 'CVR',
  'Civil Service': 'CVS',
  'Cooperative Corporations': 'CCO',
  'Correction': 'COR',
  'County': 'CNT',
  'Criminal Procedure': 'CPL',
  'Debtor & Creditor': 'DCD',
  'Domestic Relations': 'DOM',
  'Economic Development Law': 'COM',
  'Education': 'EDN',
  'Elder': 'ELD',
  'Election': 'ELN',
  'Eminent Domain Procedure': 'EDP',
  'Employers\' Liability': 'EML',
  'Energy': 'ENG',
  'Environmental Conservation': 'ENV',
  'Estates, Powers & Trusts': 'EPT',
  'Executive': 'EXC',
  'Financial Services Law': 'FIS',
  'General Associations': 'GAS',
  'General Business': 'GBS',
  'General City': 'GCT',
  'General Construction': 'GCN',
  'General Municipal': 'GMU',
  'General Obligations': 'GOB',
  'Highway': 'HAY',
  'Indian': 'IND',
  'Insurance': 'ISC',
  'Judiciary': 'JUD',
  'Labor': 'LAB',
  'Legislative': 'LEG',
  'Lien': 'LIE',
  'Limited Liability Company Law': 'LLC',
  'Local Finance': 'LFN',
  'Mental Hygiene': 'MHY',
  'Military': 'MIL',
  'Multiple Dwelling': 'MDW',
  'Multiple Residence': 'MRE',
  'Municipal Housing Authorities': 'MHA',
  'Municipal Home Rule': 'MHR',
  'Navigation': 'NAV',
  'New York State Printing and Public Documents': 'PPD',
  'Not-for-Profit Corporation': 'NPC',
  'Parks, Recreation and Historic Preservation': 'PAR',
  'Partnership': 'PTR',
  'Penal': 'PEN',
  'Personal Property': 'PEP',
  'Private Housing Finance': 'PVH',
  'Public Authorities': 'PBA',
  'Public Buildings': 'PBB',
  'Public Health': 'PBH',
  'Public Housing': 'PBG',
  'Public Lands': 'PBL',
  'Public Officers': 'PBO',
  'Public Service': 'PBS',
  'Racing, Pari-Mutuel Wagering and Breeding Law': 'PML',
  'Railroad': 'RRD',
  'Rapid Transit': 'RAT',
  'Real Property': 'RPP',
  'Real Property Actions & Proceedings': 'RPA',
  'Real Property Tax': 'RPT',
  'Religious Corporations': 'RCO',
  'Retirement & Social Security': 'RSS',
  'Rural Electric Cooperative': 'REL',
  'Second Class Cities': 'SCC',
  'Social Services': 'SOS',
  'Soil & Water Conservation Districts': 'SWC',
  'State': 'STL',
  'State Administrative Procedure Act': 'SAP',
  'State Finance': 'STF',
  'State Technology': 'STT',
  'Statute of Local Governments': 'SLG',
  'Tax': 'TAX',
  'Town': 'TWN',
  'Transportation': 'TRA',
  'Transportation Corporations': 'TCP',
  'Uniform Commercial Code': 'UCC',
  'Vehicle & Traffic': 'VAT',
  'Veterans\' Services': 'VET',
  'Village': 'VIL',
  'Volunteer Ambulance Workers\' Benefit': 'VAW',
  'Volunteer Firefighters\' Benefit': 'VOL',
  'Workers\' Compensation': 'WKC',
  'Boxing, Sparring and Wrestling Ch. 912/20': 'BSW',
  'Bridges and Tunnels New York/New Jersey 47/31': 'BAT',
  'Cigarettes, Cigars, Tobacco 235/52': 'CCT',
  'City of Troy Issuance of Serial Bonds': 'TRY',
  'Defense Emergency Act 1951 784/51': 'DEA',
  'Development of Port of New York 43/22': 'DPN',
  'Emergency Tenant Protection Act 576/74': 'ETP',
  'Expanded Health Care Coverage Act 703/88': 'EHC',
  'NYS Financial Emergency Act for the city of NY 868/75': 'FEA',
  'NYS Project Finance Agency Act 7/75': 'NYP',
  'Yonkers Financial Emergency Act 103/84': 'YFA',
  'Yonkers Income Tax Surcharge': 'YTS',
  'Facilities Development Corporation Act 359/68': 'FDC',
  'General City Model 772/66': 'GCM',
  'Local Emergency Housing Rent Control Act 21/62': 'LEH',
  'Emergency Housing Rent Control Law 274/46 337/61': 'ERL',
  'Lost and Strayed Animals 115/1894': 'LSA',
  'Medical Care Facilities Finance Agency 392/73': 'MCF',
  'N. Y. Wine/Grape 80/85': 'NYW',
  'New York City Health and Hospitals Corporation Act 1016/69': 'HHC',
  'Police Certain Municipalities 360/11': 'PCM',
  'Port of New York Authority 154/21': 'PNY',
  'Port of Albany 192/25': 'POA',
  'Private Activity Bond 47/90': 'PAB',
  'Regulation of Lobbying Act 1040/81': 'RLA',
  'Special Needs Housing Act 261/88': 'SNH',
  'Suffolk County Tax Act': 'SCT',
  'Tobacco Settlement Financing Corporation Act': 'TSF',
  'Urban Development Guarantee Fund of New York 175/68': 'UDG',
  'Urban Development Corporation Act 174/68': 'UDA',
  'Urban Development Research Corporation Act 173/68': 'UDR',
  'New, New York Bond Act 649/92': 'NNY',
  'Court of Claims Act': 'CTC',
  'Family Court Act': 'FCT',
  'New York City Civil Court Act': 'CCA',
  'New York City Criminal Court Act': 'CRC',
  'Surrogate\'s Court Procedure Act': 'SCP',
  'Uniform City Court Act': 'UCT',
  'Uniform District Court Act': 'UDC',
  'Uniform Justice Court Act': 'UJC',
  'Assembly Rules': 'CMA',
  'Senate Rules': 'CMS',
  'Constitution': 'CNS',
  'New York City Administrative Code': 'ADC',
  'New York City Charter': 'NYC',
}

function lawSectionToHref(lawSectionName) {
  const lawSectionCode = LAW_SECTIONS[lawSectionName];
  if (lawSectionCode) {
    return ("/laws/" + lawSectionCode);
  }
  else {
    return "/laws";
  }
}

function lawToHref(law, date) {
  const sectionCode = law.substring(0, 3)

  let href = "/laws/" + sectionCode

  // TODO: confirm that all leaves start with numbers and that all nodes start with letters
  // if first character of documentId is numerical, link to leaf, otherwise link to node
  if (/^\d$/.test(law.charAt(3))) {
    href += "/leaf/"
  }
  else {
    href += "/node/"
  }
  href = href + law.substring(3) + "?date=" + date.toISOString().slice(0, 10)

  return href;
}

function PreviousVersions({ bill }) {
  if (bill.previousVersions.size === 0) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Previous Versions of this Bill</h3>
      </header>
      <div className="mx-2">
        {bill.previousVersions.items.map((sameAs) => {
            return <BillListingQueried billInfo={sameAs} key={sameAs.basePrintNo} />
          }
        )}
      </div>
    </section>
  )
}

function AgendaCalendarReferences({ bill }) {
  if (bill.calendars.size === 0 && bill.committeeAgendas.size === 0) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Agenda/Calendar References</h3>
      </header>
      <div className="mx-5 my-3">
        {bill.committeeAgendas.items.map((agenda) => {
          return (
            <div key={`${agenda.agendaId.year}-${agenda.agendaId.number}-${agenda.committeeId.name}`}>
              <Link to={`/agendas/${agenda.agendaId.year}/${agenda.agendaId.number}/${agenda.committeeId.name}`} className="link">
                Committee Agenda #{agenda.agendaId.number} ({agenda.agendaId.year}) - {agenda.committeeId.name}
              </Link>
            </div>
          )
        })}
        {bill.calendars.items.map((cal) => {
          return (
            <div key={`${cal.year}-${cal.calendarNumber}`}>
              <Link to={`/calendars/${cal.year}/${cal.calendarNumber}#${bill.printNo}`} className="link">
                Senate Floor Calendar {cal.calendarNumber} ({cal.year})
              </Link>
            </div>
          )
        })}
      </div>
    </section>
  )
}

function TranscriptReferences({ amendment }) {
  if (amendment.transcripts.size === 0) {
    return null
  }

  return (
    <section className="mt-8">
      <header>
        <h3 className="h5">Transcript References</h3>
        <div className="mx-5 my-3">
          {amendment.transcripts.items.map((transcript) => {
            const date = new Date(transcript.dateTime);
            const formattedDate = date.toLocaleDateString([], { month: 'long', day: 'numeric', year: 'numeric' });
            const formattedTime = date.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' });
            let href = `/transcripts/session/${transcript.dateTime}/${transcript.sessionType}#bills-${amendment.session}-${amendment.basePrintNo}`;
            if (amendment.version !== '') {
              href += `?amendment=${amendment.version}`
            }
            return (
              <div key={`${transcript.dateTime}-${transcript.sessionType}`}>
                <Link to={href} target="_blank" className="link">
                  {transcript.sessionType} on {formattedDate} at {formattedTime}
                </Link>
              </div>
            )
          })}
        </div>
      </header>
    </section>
  )
}