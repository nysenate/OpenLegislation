class SelectOption {
  constructor(value, label) {
    this.value = value;
    this.label = label;
  }
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
