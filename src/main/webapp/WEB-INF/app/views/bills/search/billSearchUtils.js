import { getBillStatusTypes } from "app/apis/billGetApi";
import { getMembersApi } from "app/apis/memberApi";
import {
  SelectOption,
  yearSortOptions
} from "app/shared/Select";

/**
 * Option values for many of the fields in the Advanced Search section.
 */

export const sessionOptions = yearSortOptions(2009, true, true)

export const sortOptions = [
  new SelectOption("_score:desc,session:desc", "Relevant"),
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
    return [ new MemberSelectOption("", "Any") ].concat(res.items.map((m) => {
      return new MemberSelectOption(m.memberId, `${m.person.lastName}, ${m.person.firstName}`, m.chamber)
    }))
  })
}

function MemberSelectOption(value, label, chamber) {
  return {
    label: label,
    value: value,
    chamber: chamber
  }
}

export const fetchStatusTypes = () => {
  return getBillStatusTypes().then((res) => {
    return [ new SelectOption("", "Any") ].concat(res.result.items.map((status) => {
      return new SelectOption(status.name, status.description)
    }))
  })
}

/**
 * Static data which is used to configure various input's that are used in the advanced bill search.
 */
export const initialRefineState = {
  chamber: {
    type: "select",
    label: "Chamber",
    value: "",
    options: chamberOptions,
    searchTerm: function (value) {
      return value ? `billType.chamber:(${value})` : ""
    }
  },
  billType: {
    type: "select",
    label: "Bill/Resolution",
    value: "",
    options: billTypeOptions,
    searchTerm: function (value) {
      return (value === billTypeOptions[2].value) ? "billType.resolution:(true)" : "billType.resolution:(false)"
    }
  },
  sponsor: {
    type: "select",
    label: "Primary Sponsor",
    value: "",
    options: [],
    searchTerm: function (value) {
      return value ? `sponsor.member.memberId:(${value})` : ""
    }
  },
  statusType: {
    type: "select",
    label: "Current Status",
    value: "",
    options: [],
    searchTerm: function (value) {
      return value ? `status.statusType:(${value})` : ""
    }
  },
  printNo: {
    type: "input",
    label: "Print No",
    value: "",
    placeholder: "S1234",
    searchTerm: function (value) {
      return value ? `printNo:(${value})` : ""
    }
  },
  memo: {
    type: "input",
    label: "Memo",
    value: "",
    placeholder: "",
    searchTerm: function (value) {
      return value ? `amendments.\\*.memo:(${value})` : ""
    }
  },
  actionText: {
    type: "input",
    label: "Contains Action Text",
    value: "",
    placeholder: "Substituted For *",
    searchTerm: function (value) {
      return value ? `actions.\\*.text:(${value})` : ""
    }
  },
  calendarNo: {
    type: "input",
    label: "Bill Calendar Number",
    value: "",
    placeholder: "123",
    searchTerm: function (value) {
      return value ? `\\*.billCalNo:(${value})` : ""
    }
  },
  lawSection: {
    type: "input",
    label: "Law Section",
    value: "",
    placeholder: "Education",
    searchTerm: function (value) {
      return value ? `amendments.\\*.lawSection:(${value})` : ""
    }
  },
  title: {
    type: "input",
    label: "Title",
    value: "",
    placeholder: "Title of the bill/reso",
    searchTerm: function (value) {
      return value ? `title:(${value})` : ""
    }
  },
  fullText: {
    type: "input",
    label: "Full Text",
    value: "",
    placeholder: "",
    searchTerm: function (value) {
      return value ? `amendments.\\*.fullText:(${value})` : ""
    }
  },
  committee: {
    type: "input",
    label: "In Committee (Name)",
    value: "",
    placeholder: "Aging",
    searchTerm: function (value) {
      return value ? `status.committeeName:(${value})` : ""
    }
  },
  agendaNo: {
    type: "input",
    label: "Agenda Number",
    value: "",
    placeholder: "4",
    searchTerm: function (value) {
      return value ? `\\*.agendaId.number:(${value})` : ""
    }
  },
  lawCode: {
    type: "input",
    label: "Law Code",
    value: "",
    placeholder: "236 Town L",
    searchTerm: function (value) {
      return value ? `amendments.\\*.lawCode:(${value})` : ""
    }
  },
  isSigned: {
    type: "checkbox",
    label: "Is Signed / Adopted",
    value: "",
    searchTerm: function (value) {
      return value ? "(signed:true OR adopted:true)" : ""
    }
  },
  isGovernorBill: {
    type: "checkbox",
    label: "Is Governor's Bill",
    value: false,
    searchTerm: function (value) {
      return value ? "(programInfo.name:*Governor*)" : ""
    }
  },
  hasVotes: {
    type: "checkbox",
    label: "Has Votes",
    value: false,
    searchTerm: function (value) {
      return value ? "(votes.size:>0)" : ""
    }
  },
  hasApVetoMemo: {
    type: "checkbox",
    label: "Has Appr/Veto Memo",
    value: false,
    searchTerm: function (value) {
      return value ? "(vetoMessages.size:>0 OR !_empty_:approvalMessage)" : ""
    }
  },
  isSubstituted: {
    type: "checkbox",
    label: "Is Substituted By",
    value: false,
    searchTerm: function (value) {
      return value ? "(_exists_:substitutedBy)" : ""
    }
  },
  isUniBill: {
    type: "checkbox",
    label: "Is Uni Bill",
    value: false,
    searchTerm: function (value) {
      return value ? "(amendments.\\*.uniBill:true)" : ""
    }
  },
  isBudgetBill: {
    type: "checkbox",
    label: "Is Budget Bill",
    value: false,
    searchTerm: function (value) {
      return value ? "(sponsor.budget:true)" : ""
    }
  },
  isRulesSponsored: {
    type: "checkbox",
    label: "Is Rules Sponsored",
    value: false,
    searchTerm: function (value) {
      return value ? "(sponsor.rules:true)" : ""
    }
  },
}
