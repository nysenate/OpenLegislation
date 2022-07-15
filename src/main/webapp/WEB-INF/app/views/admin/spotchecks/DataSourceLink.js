import React from 'react'
import useGlobals from "app/shared/useGlobals";


export default function DataSourceLink({ datasource, contentType, contentKey }) {
  const globals = useGlobals()
  const [ url, setUrl ] = React.useState()

  React.useEffect(() => {
    switch (datasource) {
      case "LBDC":
        setUrl(getLBDCUrl(contentType, contentKey))
        break
      case "NYSENATE":
        setUrl(getNYSenateUrl(contentType, contentKey, globals))
        break
      case "OPENLEG": // Openleg Reference
        setUrl(getOLRefUrl(contentType, contentKey, globals))
        break
      case "OPENLEGLOCAL": // The local, running, version of OpenLegislation
        setUrl(getOLLocalUrl(contentType, contentKey))
        break
    }
  }, [ datasource, contentType, contentKey ])

  if (!url) {
    <span className="text-white">{datasourceDisplayNames[datasource]}</span>
  }
  return <a href={url}
            className="text-white border-white border-solid border-b-1 cursor-pointer"
            target="_blank">{datasourceDisplayNames[datasource]}</a>
}

const datasourceDisplayNames = {
  LBDC: "LBDC",
  NYSENATE: "NY Senate",
  OPENLEG: "Open Legislation Reference",
  OPENLEGLOCAL: "Open Legislation Local",
}

function getLBDCUrl(contentType, contentKey) {
  // We can only link to Bills on LBDC.
  switch (contentType) {
    case "BILL":
      return `http://public.leginfo.state.ny.us/navigate.cgi
      ?NVDTO:=&QUERYTYPE=BILLNO&CBTEXT=Y&CBSPONMEMO=Y
      &SESSYR=${contentKey.session.year}&QUERYDATA=${contentKey.printNo}j`
    default:
      return ""
  }
}

function getNYSenateUrl(contentType, contentKey, globals) {
  switch (contentType) {
    case "BILL_AMENDMENT":
      return billAmendmentUrl();
    case "LAW":
      return lawUrl();
    case "AGENDA":
    // Can't link to agendas without the meeting date time
    // Example link: https://www.nysenate.gov/calendar/meetings/codes/january-23-2017/codes-meeting
    // Fall through to default case for now.
    case "CALENDAR":
    // Can't link to calendars without the session date time
    // Example link: https://www.nysenate.gov/calendar/sessions/june-05-2017/session-6-5-17
    // Fall through to default case for now.
    default:
      return "NYSenate"
  }

  function billAmendmentUrl() {
    let billType = "bills"
    if (!/^[SA]/i.test(contentKey.printNo)) {
      billType = "resolutions"
    }
    let amendment = contentKey.version === "DEFAULT" ? "original" : contentKey.version
    return `${globals.senSitePath}/legislation/${billType}/${contentKey.session.year}/${contentKey.basePrintNo}/amendment/${amendment}`
  }

  function lawUrl() {
    let url = `${globals.senSitePath}/legislation/laws`
    switch (contentKey.obsType) {
      case "TREE":
        url += `/${contentKey.lawChapter}`
        break
      case "DOCUMENT":
        url += `/${contentKey.lawChapter}/${contentKey.locationId}`
        break
      default:
        url += "/all"
    }
    return url;
  }
}

function getOLRefUrl(contentType, contentKey, globals) {
  let url = globals.openlegRefPath
  switch (contentType) {
    case "BILL":
      url += `/bills/${contentKey.session.year}/${contentKey.basePrintNo}`
      url += contentKey.hasOwnProperty('version') ? `?version=${contentKey.version}` : ""
      break
    case "AGENDA":
      url += `/agendas/${contentKey.agendaId.year}/${contentKey.agendaId.number}?comm=${contentKey.committeeId.name}`
      break
    case "CALENDAR":
      url += `/calendars/${contentKey.year}/${contentKey.calNo}`
      if (contentKey.hasOwnProperty("type")) {
        switch (contentKey.type) {
          case 'ACTIVE_LIST':
            url += '?view=active-list'
            break
          case 'FLOOR_CALENDAR':
          case 'SUPPLEMENTAL_CALENDAR':
            url += '?view=floor'
            break
        }
      }
      break
  }
  return url;
}

function getOLLocalUrl(contentType, contentKey) {
  let url = ""
  switch (contentType) {
    case "BILL": // fall through
    case "BILL_AMENDMENT":
      url += `/bills/${contentKey.session.year}/${contentKey.basePrintNo}?amendment=${contentKey.version || ""}`
      break
    case "AGENDA": // fall through
    case "AGENDA_WEEK":
      url += `/agendas`
      if (contentKey.agendaId) {
        url += `/${contentKey.agendaId.year}/${contentKey.agendaId.number}`
      }
      if (contentKey.committeeId) {
        url += `/${contentKey.committeeId.name}`
      }
      break
    case "CALENDAR":
      url += `/calendars/${contentKey.year}/${contentKey.calNo}`
      break
    case "LAW":
      url += `/laws/${contentKey.lawChapter}`
      break
  }
  return url
}