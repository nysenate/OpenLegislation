import React from "react"
import { Link } from "react-router-dom";

export function BillLawLink({ action, isPassed, law, date }) {
  if (date < earliestLawTree()) {
    return (
      <span>{law}</span>
    )
  }
  // show old and new version of law
  else if (action === 'AMEND' && isPassed) {
    return (
      <span>
        {law}&nbsp;(<Link to={lawToHref(law, date, false)} target="_blank" className="link">old</Link>,&nbsp;
        <Link to={lawToHref(law, date, true)} target="_blank" className="link">new</Link>)
      </span>
    )
  }
  // only show old version of law
  else if ((action === 'AMEND' && !isPassed) ||
          new Set(['REPEAL', 'RENAME', 'RENUMERATE', 'RELETTER', 'DESIGNATE', 'REDESIGNATE', 'RELETTER', 'RENUMBER']).has(action)) {
    return (
      <Link to={lawToHref(law, date, false)} target="_blank" className="link">{law}</Link>
    )
  }
  // only show new version of law
  else if (action === 'ADD' && isPassed) {
    return (
      <Link to={lawToHref(law, date, true)} target="_blank" className="link">{law}</Link>
    )
  }
  // do not link to law
  else {
    return (
      <span>{law}</span>
    )
  }
}

export function BillLawChapterLink({ lawChapterName, date }) {
  const chapterCode = LAW_CHAPTERS[lawChapterName] ??
    LAW_CHAPTERS[lawChapterName.replace(/ Law/g, "")] ??
    LAW_CHAPTERS[lawChapterName.replace(/s$/, "")]

  if (!chapterCode || date < earliestLawTree()) {
    return (
      <p className="text">Primary Law Section - {lawChapterName}</p>
    )
  }
  else {
    const chapterLink = "/laws/" + chapterCode + "?date=" + date
    return (
      <Link to={chapterLink} target="_blank" className="link">Primary Law Section - {lawChapterName}</Link>
    )
  }
}

function lawToHref(law, date, findNext) {
  const sectionCode = law.substring(0, 3)

  let href = "/laws/" + sectionCode

  // if first character of documentId is numerical, link to leaf, otherwise link to node
  if (/^\d$/.test(law.charAt(3))) {
    href += "/leaf/"
  }
  else {
    href += "/node/"
  }
  href = href + law.substring(3) + "?date=" + date + "&findNext=" + findNext

  return href;
}

/**
 * There are no law trees before the returned date
 */
function earliestLawTree() {
  return '2014-09-22'
}

/**
 * LawChapterCode enum with some modifications so that it matches actual bill data. Some keys are unused, and some chapters have multiple keys.
 * There are a number of law sections in bills that do not match to any particular law chapter.
 */
const LAW_CHAPTERS = {
  'Abandoned Property': 'ABP',
  'Agriculture and Markets': 'AGM',
  'Alcoholic Beverage Control': 'ABC',
  'Alternative County Government': 'ACG', // no matches
  'Arts and Cultural Affairs': 'ACA',
  'Banking': 'BNK',
  'Banks': 'BNK',
  'Benevolent Orders': 'BVO',
  'Business Corporation': 'BSC',
  'Canal': 'CAL',
  'Cannabis': 'CAN',
  'Civil Practice Law and Rules': 'CVP',
  'Civil Rights': 'CVR',
  'Civil Service': 'CVS',
  'Cooperative Corporations': 'CCO',
  'Correction': 'COR',
  'County': 'CNT',
  'Counties': 'CNT',
  'Criminal Procedure': 'CPL',
  'Debtor and Creditor': 'DCD',
  'Domestic Relations': 'DOM',
  'Economic Development': 'COM',
  'Education': 'EDN',
  'Elder': 'ELD',
  'Election': 'ELN',
  'Eminent Domain Procedure': 'EDP',
  'Employers\' Liability': 'EML',
  'Energy': 'ENG',
  'Environmental Conservation': 'ENV',
  'Estates, Powers and Trusts': 'EPT',
  'Executive': 'EXC',
  'Financial Services': 'FIS',
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
  'Limited Liability Company': 'LLC',
  'Local Finance': 'LFN',
  'Mental Hygiene': 'MHY',
  'Military': 'MIL',
  'Multiple Dwelling': 'MDW',
  'Multiple Residence': 'MRE',
  'Municipal Housing Authorities': 'MHA', // no matches
  'Municipal Home Rule': 'MHR',
  'Navigation': 'NAV',
  'New York State Printing and Public Documents': 'PPD',
  'Not-for-Profit Corporation': 'NPC',
  'Parks, Recreation and Historic Preservation': 'PAR',
  'Partnership': 'PTR', // no matches
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
  'Racing, Pari-Mutuel Wagering and Breeding': 'PML',
  'Railroad': 'RRD',
  'Rapid Transit': 'RAT', // no matches
  'Real Property': 'RPP',
  'Real Property Actions and Proceedings': 'RPA',
  'Real Property Tax': 'RPT',
  'Real Property Taxation': 'RPT',
  'Religious Corporations': 'RCO',
  'Retirement and Social Security': 'RSS',
  'Rural Electric Cooperative': 'REL',
  'Second Class Cities': 'SCC', // no matches
  'Social Services': 'SOS',
  'Soil and Water Conservation Districts': 'SWC',
  'State': 'STL',
  'State Administrative Procedure Act': 'SAP',
  'State Finance': 'STF',
  'State Technology': 'STT',
  'Statute of Local Governments': 'SLG', // no matches
  'Tax': 'TAX',
  'Town': 'TWN',
  'Transportation': 'TRA',
  'Transportation Corporations': 'TCP',
  'Uniform Commercial Code': 'UCC',
  'Vehicle and Traffic': 'VAT',
  'Veterans': 'VET',
  'Veterans\' Service': 'VET',
  'Village': 'VIL',
  'Volunteer Ambulance Workers\' Benefit': 'VAW',
  'Volunteer Firefighters\' Benefit': 'VOL',
  'Workers\' Compensation': 'WKC',
  'Court of Claims Act': 'CTC',
  'Family Court Act': 'FCT',
  'New York City Civil Court Act': 'CCA',
  'New York City Criminal Court Act': 'CRC',
  'Surrogate\'s Court Procedure Act': 'SCP',
  'Uniform City Court Act': 'UCT',
  'Uniform District Court Act': 'UDC',
  'Uniform Justice Court Act': 'UJC',
  'Assembly Rules': 'CMA',
  'Resolutions, Assembly': 'CMA',
  'Senate Rules': 'CMS',
  'Resolutions, Senate': 'CMS',
  'Constitution': 'CNS',
  'Constitution, Concurrent Resolutions to Amend': 'CNS',
  'New York City Administrative Code': 'ADC',
  'New York City Charter': 'NYC',

  // most unconsolidated law chapters do not match properly
  'Boxing, Sparring and Wrestling': 'BSW',
  'Boxing': 'BSW',
  'Bridges and Tunnels New York/New Jersey': 'BAT',
  'Cigarettes, Cigars, Tobacco': 'CCT',
  'City of Troy Issuance of Serial Bonds': 'TRY',
  'Defense Emergency Act 1951': 'DEA',
  'Development of Port of New York': 'DPN',
  'Emergency Tenant Protection Act': 'ETP',
  'Expanded Health Care Coverage Act': 'EHC',
  'NYS Financial Emergency Act for the city of NY': 'FEA',
  'NYS Project Finance Agency Act': 'NYP',
  'Yonkers Financial Emergency Act': 'YFA',
  'Yonkers Income Tax Surcharge': 'YTS',
  'Facilities Development Corporation Act': 'FDC',
  'General City Model': 'GCM',
  'Local Emergency Housing Rent Control Act': 'LEH',
  'Emergency Housing Rent Control': 'ERL',
  'Lost and Strayed Animals': 'LSA',
  'Medical Care Facilities Finance Agency': 'MCF',
  'N. Y. Wine/Grape': 'NYW',
  'New York City Health and Hospitals Corporation Act': 'HHC',
  'Police Certain Municipalities': 'PCM',
  'Port of New York Authority': 'PNY',
  'Port of Albany': 'POA',
  'Private Activity Bond': 'PAB',
  'Regulation of Lobbying Act': 'RLA',
  'Special Needs Housing Act': 'SNH',
  'Suffolk County Tax Act': 'SCT',
  'Tobacco Settlement Financing Corporation Act': 'TSF',
  'Urban Development Guarantee Fund of New York': 'UDG',
  'Urban Development Corporation Act': 'UDA',
  'Urban Development Research Corporation Act': 'UDR',
  'New, New York Bond Act': 'NNY',
}