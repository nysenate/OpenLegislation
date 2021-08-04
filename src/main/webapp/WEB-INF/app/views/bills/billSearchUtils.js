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
  return handleEmptyValue(value, `billType.chamber:(${value})`)
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
  return handleEmptyValue(value, `billType.resolution:(${value === "Resolution" ? "true" : "false"})`)
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
  return handleEmptyValue(value, `sponsor.member.memberId:(${value})`)
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
  return handleEmptyValue(value, `status.statusType:(${value})`)
}


/**
 * PrintNo
 */

export const printNoTerm = (value) => {
  return handleEmptyValue(value, `printNo:(${value})`)
}

/**
 * Memo
 */

export const memoTerm = (value) => {
  return handleEmptyValue(value, `amendments.\\*.memo:(${value})`)
}

/**
 * ActionText
 */

export const actionTextTerm = (value) => {
  return handleEmptyValue(value, `actions.\\*.text:(${value})`)
}


/**
 * Calendar Number
 */

export const calendarNoTerm = (value) => {
  return handleEmptyValue(value, `\\*.billCalNo:(${value})`)
}

/**
 * Law Section
 */

export const lawSectionTerm = (value) => {
  return handleEmptyValue(value, `amendments.\\*.lawSection:(${value})`)
}

/**
 * Title
 */

export const titleTerm = (value) => {
  return handleEmptyValue(value, `title:(${value})`)
}

/**
 * Full Text
 */

export const fullTextTerm = (value) => {
  return handleEmptyValue(value, `amendments.\\*.fullText:(${value})`)
}

/**
 * Committee
 */
export const committeeTerm = (value) => {
  return handleEmptyValue(value, `status.committeeName:(${value})`)
}

/**
 * Agenda Number
 */
export const agendaNoTerm = (value) => {
  return handleEmptyValue(value, `\\*.agendaId.number:(${value})`)
}

/**
 * Law Code
 */
export const lawCodeTerm = (value) => {
  return handleEmptyValue(value, `amendments.\\*.lawCode:(${value})`)
}

/**
 * Misc
 */

const handleEmptyValue = (value, termString) => {
  if (!value) {
    return ""
  }
  return termString
}