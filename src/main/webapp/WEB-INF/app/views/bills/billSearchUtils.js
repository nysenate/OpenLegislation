import { billSessionYears } from "app/lib/dateUtils";
import { getBillStatusTypes } from "app/apis/billGetApi";
import { getMembersApi } from "app/apis/memberApi";

export const initialRefineState = {
  chamber: {
    type: "select",
    label: "Chamber",
    value: "",
    options: chamberOptions,
    searchTerm: function () {
      this.value ? `billType.chamber(${this.value})` : ""
    }
  },
  billType: {
    type: "select",
    label: "Bill/Resolution",
    value: "",
    options: billTypeOptions,
    searchTerm: function () {
      this.value ? "billType.resolution:(true)" : ""
    }
  },
  sponsor: {
    type: "select",
    label: "Primary Sponsor",
    value: "",
    options: [],
    searchTerm: function () {
      this.value ? `sponsor.member.memberId:(${this.value})` : ""
    }
  },
  statusType: {
    type: "select",
    label: "Current Status",
    value: "",
    options: [],
    searchTerm: function () {
      this.value ? `status.statusType:(${this.value})` : ""
    }
  },
  printNo: {
    type: "input",
    label: "Print No",
    value: "",
    placeholder: "S1234",
    searchTerm: function () {
      this.value ? `printNo:(${this.value})` : ""
    }
  },
  memo: {
    type: "input",
    label: "Memo",
    value: "",
    placeholder: "",
    searchTerm: function () {
      this.value ? `amendments.\\*.memo:(${this.value})` : ""
    }
  },
  actionText: {
    type: "input",
    label: "Contains Action Text",
    value: "",
    placeholder: "Substituted For *",
    searchTerm: function () {
      this.value ? `actions.\\*.text:(${this.value})` : ""
    }
  },
  calendarNo: {
    type: "input",
    label: "Bill Calendar Number",
    value: "",
    placeholder: "123",
    searchTerm: function () {
      this.value ? `\\*.billCalNo:(${this.value})` : ""
    }
  },
  lawSection: {
    type: "input",
    label: "Law Section",
    value: "",
    placeholder: "Education",
    searchTerm: function () {
      this.value ? `amendments.\\*.lawSection:(${this.value})` : ""
    }
  },
  title: {
    type: "input",
    label: "Title",
    value: "",
    placeholder: "Title of the bill/reso",
    searchTerm: function () {
      this.value ? `title:(${this.value})` : ""
    }
  },
  fullText: {
    type: "input",
    label: "Full Text",
    value: "",
    placeholder: "",
    searchTerm: function () {
      this.value ? `amendments.\\*.fullText:(${this.value})` : ""
    }
  },
  committee: {
    type: "input",
    label: "In Committee (Name)",
    value: "",
    placeholder: "Aging",
    searchTerm: function () {
      this.value ? `status.committeeName:(${this.value})` : ""
    }
  },
  agendaNo: {
    type: "input",
    label: "Agenda Number",
    value: "",
    placeholder: "4",
    searchTerm: function () {
      this.value ? `\\*.agendaId.number:(${this.value})` : ""
    }
  },
  lawCode: {
    type: "input",
    label: "Law Code",
    value: "",
    placeholder: "236 Town L",
    searchTerm: function () {
      this.value ? `amendments.\\*.lawCode:(${this.value})` : ""
    }
  },
  isSigned: {
    type: "checkbox",
    label: "Is Signed / Adopted",
    value: false,
    searchTerm: function () {
      this.value ? "(signed:true OR adopted:true)" : ""
    }
  },
  isGovernorBill: {
    type: "checkbox",
    label: "Is Governor's Bill",
    value: false,
    searchTerm: function () {
      this.value ? "(programInfo.name:*Governor*)" : ""
    }
  },
  hasVotes: {
    type: "checkbox",
    label: "Has Votes",
    value: false,
    searchTerm: function () {
      this.value ? "(votes.size:>0)" : ""
    }
  },
  hasApVetoMemo: {
    type: "checkbox",
    label: "Has Appr/Veto Memo",
    value: false,
    searchTerm: function () {
      this.value ? "(vetoMessages.size:>0 OR !_empty_:approvalMessage)" : ""
    }
  },
  isSubstituted: {
    type: "checkbox",
    label: "Is Substituted By",
    value: false,
    searchTerm: function () {
      this.value ? "(_exists_:substitutedBy)" : ""
    }
  },
  isUniBill: {
    type: "checkbox",
    label: "Is Uni Bill",
    value: false,
    searchTerm: function () {
      this.value ? "(amendments.\\*.uniBill:true)" : ""
    }
  },
  isBudgetBill: {
    type: "checkbox",
    label: "Is Budget Bill",
    value: false,
    searchTerm: function () {
      this.value ? "(sponsor.budget:true)" : ""
    }
  },
  isRulesSponsored: {
    type: "checkbox",
    label: "Is Rules Sponsored",
    value: false,
    searchTerm: function () {
      this.value ? "(sponsor.rules:true)" : ""
    }
  },
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
