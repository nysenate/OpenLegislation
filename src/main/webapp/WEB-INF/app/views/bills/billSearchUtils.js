import { billSessionYears } from "app/lib/dateUtils";
import { getBillStatusTypes } from "app/apis/billGetApi";
import { getMembersApi } from "app/apis/memberApi";

/**
 * To filter by advanced search fields, a lucene query string has to be added to the search term.
 * These are the templates for how each should be added.
 */
export const REFINE = {
  PATHS: {
    actionText: "actions.\\*.text",
    agendaNo: "\\*.agendaId.number",
    calendarNo: "\\*.billCalNo",
    chamber: "billType.chamber",
    committee: "status.committeeName",
    fullText: "amendments.\\*.fullText",
    lawCode: "amendments.\\*.lawCode",
    lawSection: "amendments.\\*.lawSection",
    memo: "amendments.\\*.memo",
    printNo: "printNo",
    sponsor: "sponsor.member.memberId",
    statusType: "status.statusType",
    title: "title"
  },
  FIXED_PATHS: {
    isSigned: '(signed:true OR adopted:true)',
    hasVotes: '(votes.size:>0)',
    hasApVetoMemo: '(vetoMessages.size:>0 OR !_empty_:approvalMessage)',
    isGovernorBill: '(programInfo.name:*Governor*)',
    isSubstituted: '(_exists_:substitutedBy)',
    isUniBill: '(amendments.\\*.uniBill:true)',
    isBudgetBill: '(sponsor.budget:true)',
    isRulesSponsored: '(sponsor.rules:true)',
    billType: 'billType.resolution:(true)'
  }
}

class SelectOption {
  constructor(value, label) {
    this.value = value;
    this.label = label;
  }
}

/**
 * Option values for many of the fields in the Advanced Search section.
 */

export const sessionOptions = () => {
  let sessions = billSessionYears().map((year) => new SelectOption(year, year))
  sessions.unshift(new SelectOption("", "Any"))
  return sessions
}

export const sortOptions = [
  new SelectOption("_score:desc,session:desc", "Relavent"),
  new SelectOption("status.actionDate:desc,_score:desc", "Recent Status Update"),
  new SelectOption("printNo:asc,session:desc", "Print No"),
  new SelectOption("milestones.size:desc,_score:desc", "Most Progress"),
  new SelectOption("amendments.size:desc,_score:desc", "Most Amendments")
]

export const chamberOptions = [
  new SelectOption("", "Any"),
  new SelectOption("SENATE", "Senate"),
  new SelectOption("ASSEMBLY", "Assembly")
]

export const billTypeOptions = [
  new SelectOption("", "Any"),
  new SelectOption("Bill", "Bill"),
  new SelectOption("Resolution", "Resolution")
]

export const fetchMembers = (session) => {
  return getMembersApi(session).then((res) => {
    return [ new SelectOption("", "Any") ].concat(res.items.map((m) => {
      return new SelectOption(m.memberId, `${m.person.lastName}, ${m.person.firstName}`)
    }))
  })
}

export const fetchStatusTypes = () => {
  return getBillStatusTypes().then((res) => {
    return [ new SelectOption("", "Any") ].concat(res.items.map((status) => {
      return new SelectOption(status.name, status.description)
    }))
  })
}
