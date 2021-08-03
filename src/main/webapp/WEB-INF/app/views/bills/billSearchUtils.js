import { billSessionYears } from "app/lib/dateUtils";
import { getBillStatusTypes } from "app/apis/billGetApi";
import { getMembersApi } from "app/apis/memberApi";

export class SelectOption {
  constructor(value, label) {
    this.value = value;
    this.label = label;
  }
}

/**
 * Session year Utils
 */

export const sessionOptions = () => {
  let sessions = billSessionYears().map((year) => new SelectOption(year, year))
  sessions.unshift(new SelectOption("", "Any"))
  return sessions
}

/**
 * Sort Utils
 */

// TODO finish converting sort options to use this data
export const sortOptions = [
  new SelectOption("_score:desc,session:desc", "Relavent"),
  new SelectOption("status.actionDate:desc,_score:desc", "Recent Status Update"),
  new SelectOption("printNo:asc,session:desc", "Print No"),
  new SelectOption("milestones.size:desc,_score:desc", "Most Progress"),
  new SelectOption("amendments.size:desc,_score:desc", "Most Amendments")
]

/**
 * Chamber Utils
 */
export const chamberOptions = [
  new SelectOption("", "Any"),
  new SelectOption("SENATE", "Senate"),
  new SelectOption("ASSEMBLY", "Assembly")
]

export const chamberSearchTerm = (value) => {
  if (!value) {
    return "";
  }
  return `billType.chamber:(${value})`
}


/**
 * Bill Type Utils
 */
export const billTypeOptions = [
  new SelectOption("", "Any"),
  new SelectOption("Bill", "Bill"),
  new SelectOption("Resolution", "Resolution")
]

export const billTypeSearchTerm = (value) => {
  if (!value) {
    return ""
  }
  return `billType.resolution:(${value === "Resolution" ? "true" : "false"})`
}

/**
 * Members/Sponsors
 */

export const fetchMembers = (session) => {
  return getMembersApi(session).then((res) => {
    return [ new SelectOption("", "Any") ].concat(res.items.map((m) => {
      return new SelectOption(m.memberId, `${m.person.lastName}, ${m.person.firstName}`)
    }))
  })
}

export const sponsorSearchTerm = (value) => {
  if (!value) {
    return ""
  }
  return `sponsor.member.memberId:(${value})`
}

/**
 * Bill Status Types
 */

export const fetchStatusTypes = () => {
  return getBillStatusTypes().then((res) => {
    return [ new SelectOption("", "Any") ].concat(res.items.map((status) => {
      return new SelectOption(status.name, status.description)
    }))
  })
}

export const statusTypeTerm = (value) => {
  if (!value) {
    return ""
  }
  return `status.statusType:(${value})`
}
