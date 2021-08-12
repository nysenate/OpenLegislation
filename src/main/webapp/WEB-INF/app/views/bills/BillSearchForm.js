import React from 'react'
import {
  useHistory,
  useLocation
} from "react-router-dom";
import * as queryString from "query-string";
import Accordion from "app/shared/Accordion";
import { Sliders } from "phosphor-react";
import {
  billTypeOptions,
  chamberOptions,
  fetchMembers,
  fetchStatusTypes,
  sessionOptions,
  sortOptions
} from "app/views/bills/billSearchUtils";


const advancedSearchTitleEls = (
  <div className="flex items-center">
    <Sliders size="1.25rem" className="inline-block mr-2" />
    <h4 className="inline-block">Advanced Search Options</h4>
  </div>
)

export default function BillSearchForm() {
  const [ term, setTerm ] = React.useState("")
  const [ sort, setSort ] = React.useState(sortOptions[0].value)
  const [ session, setSession ] = React.useState(sessionOptions()[0].value)
  const [ chamber, setChamber ] = React.useState(chamberOptions[0].value)
  const [ billType, setBillType ] = React.useState(billTypeOptions[0].value)
  const [ sponsor, setSponsor ] = React.useState()
  const [ sponsorOptions, setSponsorOptions ] = React.useState()
  const [ statusType, setStatusType ] = React.useState()
  const [ statusTypeOptions, setStatusTypeOptions ] = React.useState()
  const [ printNo, setPrintNo ] = React.useState()
  const [ memo, setMemo ] = React.useState()
  const [ actionText, setActionText ] = React.useState()
  const [ calendarNo, setCalendarNo ] = React.useState()
  const [ lawSection, setLawSection ] = React.useState()
  const [ title, setTitle ] = React.useState()
  const [ fullText, setFullText ] = React.useState()
  const [ committee, setCommittee ] = React.useState()
  const [ agendaNo, setAgendaNo ] = React.useState()
  const [ lawCode, setLawCode ] = React.useState()
  const [ isSigned, setIsSigned ] = React.useState(false)
  const [ isGovernorBill, setIsGovernorBill ] = React.useState(false)
  const [ hasVotes, setHasVotes ] = React.useState(false)
  const [ hasApVetoMemo, setHasApVetoMemo ] = React.useState(false)
  const [ isSubstituted, setIsSubstituted ] = React.useState(false)
  const [ isUniBill, setIsUniBill ] = React.useState(false)
  const [ isBudgetBill, setIsBudgetBill ] = React.useState(false)
  const [ isRulesSponsored, setIsRulesSponsored ] = React.useState(false)
  const location = useLocation()
  const history = useHistory()

  React.useEffect(() => {
    fetchStatusTypes().then((types) => {
      setStatusTypeOptions(types)
    })
  }, [])

  React.useEffect(() => {
    fetchMembers(session).then((members) => {
      setSponsorOptions(members)
    })
  }, [ session ])

  // Update search fields when back/forward navigation is used.
  React.useEffect(() => {
    const params = queryString.parse(location.search, { parseBooleans: true })
    setTerm(params.term || "")
    setSort(params.sort || sortOptions[0].value)
    setSession(params.session || sessionOptions()[0].value)
    setChamber(params.chamber || chamberOptions[0].value)
    setBillType(params.billType || billTypeOptions[0].value)
    if (sponsorOptions) {
      setSponsor(params.sponsor || sponsorOptions[0].value)
    }
    if (statusTypeOptions) {
      setStatusType(params.statusType || statusTypeOptions[0].value)
    }
    setPrintNo(params.printNo || "")
    setMemo(params.memo || "")
    setActionText(params.actionText || "")
    setCalendarNo(params.calendarNo || "")
    setLawSection(params.lawSection || "")
    setTitle(params.title || "")
    setFullText(params.fullText || "")
    setCommittee(params.committee || "")
    setAgendaNo(params.agendaNo || "")
    setLawCode(params.lawCode || "")
    setIsSigned(params.isSigned)
    setIsGovernorBill(params.isGovernorBill)
    setHasVotes(params.hasVotes)
    setHasApVetoMemo(params.hasApVetoMemo)
    setIsSubstituted(params.isSubstituted)
    setIsUniBill(params.isUniBill)
    setIsBudgetBill(params.isBudgetBill)
    setIsRulesSponsored(params.isRulesSponsored)
  }, [ location ])

  // Updates the term query param when the form is submitted.
  const onSubmit = (e) => {
    e.preventDefault()
    const params = queryString.parse(location.search)
    params.page = 1 // Reset to page 1.
    params.term = term
    params.sort = sort
    params.session = session
    params.chamber = chamber
    params.billType = billType
    params.sponsor = sponsor
    params.statusType = statusType
    params.printNo = printNo
    params.memo = memo
    params.actionText = actionText
    params.calendarNo = calendarNo
    params.lawSection = lawSection
    params.title = title
    params.fullText = fullText
    params.committee = committee
    params.agendaNo = agendaNo
    params.lawCode = lawCode
    params.isSigned = isSigned
    params.isGovernorBill = isGovernorBill
    params.hasVotes = hasVotes
    params.hasApVetoMemo = hasApVetoMemo
    params.isSubstituted = isSubstituted
    params.isUniBill = isUniBill
    params.isBudgetBill = isBudgetBill
    params.isRulesSponsored = isRulesSponsored
    history.push({ search: queryString.stringify(params) })
  }

  const filterWrapperClass = "mx-4 my-2"
  const advancedFilterColumnClass = "flex flex-col w-12/12 sm:w-6/12 lg:w-3/12"

  return (
    <div>
      <form onSubmit={onSubmit}>
        <div className="flex flex-wrap">
          <div className="flex-grow mr-8">
            <label htmlFor="billsearch" className="label label--top">
              Print number or term
            </label>
            <input onChange={(e) => setTerm(e.target.value)}
                   value={term}
                   tabIndex="1"
                   name="billsearch"
                   type="text"
                   className="input w-full"
                   placeholder="e.g. S1234-2015 or yogurt" />
          </div>
          <div className="mr-8">
            <SearchSelect label="Session Year"
                          tabindex={2}
                          value={session}
                          onChange={(e) => setSession(e.target.value)}
                          options={sessionOptions()} />
          </div>
          <div className="">
            <SearchSelect label="Sort By"
                          tabindex={3}
                          value={sort}
                          onChange={(e) => setSort(e.target.value)}
                          options={sortOptions} />
          </div>
        </div>

        <div className="m-4">
          <Accordion title={advancedSearchTitleEls}>
            <div className="flex justify-between flex-wrap">
              <div className={advancedFilterColumnClass}>
                <div className={filterWrapperClass}>
                  <SearchSelect label="Chamber"
                                value={chamber}
                                onChange={(e) => setChamber(e.target.value)}
                                options={chamberOptions} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchSelect label="Bill/Resolution"
                                value={billType}
                                onChange={(e) => setBillType(e.target.value)}
                                options={billTypeOptions} />
                </div>

                <div className={filterWrapperClass}>
                  <SearchSelect label="Primary Sponsor"
                                value={sponsor}
                                onChange={(e) => setSponsor(e.target.value)}
                                options={sponsorOptions} />
                </div>

                <div className={filterWrapperClass}>
                  <SearchSelect label="Current Status"
                                value={statusType}
                                onChange={(e) => setStatusType(e.target.value)}
                                options={statusTypeOptions} />
                </div>
              </div>


              <div className={advancedFilterColumnClass}>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Print no"
                                   value={printNo}
                                   onChange={(e) => setPrintNo(e.target.value)}
                                   placeholder="S1234" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Memo"
                                   value={memo}
                                   onChange={(e) => setMemo(e.target.value)}
                                   placeholder="" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Contains Action Text"
                                   value={actionText}
                                   onChange={(e) => setActionText(e.target.value)}
                                   placeholder="Substituted For *" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Bill Calendar Number"
                                   value={calendarNo}
                                   onChange={(e) => setCalendarNo(e.target.value)}
                                   placeholder="123" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Law Section"
                                   value={lawSection}
                                   onChange={(e) => setLawSection(e.target.value)}
                                   placeholder="Education" />
                </div>
              </div>

              <div className={advancedFilterColumnClass}>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Title"
                                   value={title}
                                   onChange={(e) => setTitle(e.target.value)}
                                   placeholder="Title of the bill/reso" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Full Text"
                                   value={fullText}
                                   onChange={(e) => setFullText(e.target.value)}
                                   placeholder="" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="In Committee (Name)"
                                   value={committee}
                                   onChange={(e) => setCommittee(e.target.value)}
                                   placeholder="Aging" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Agenda Number"
                                   value={agendaNo}
                                   onChange={(e) => setAgendaNo(e.target.value)}
                                   placeholder="4" />
                </div>
                <div className={filterWrapperClass}>
                  <SearchTextInput label="Law Code"
                                   value={lawCode}
                                   onChange={(e) => setLawCode(e.target.value)}
                                   placeholder="236 Town L" />
                </div>
              </div>


              <div className={advancedFilterColumnClass}>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Is Signed / Adopted"
                                  value={isSigned}
                                  onChange={(e) => setIsSigned(e.target.checked)} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Is Governor's Bill"
                                  value={isGovernorBill}
                                  onChange={(e) => setIsGovernorBill(e.target.checked)} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Has Votes"
                                  value={hasVotes}
                                  onChange={(e) => setHasVotes(e.target.checked)} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Has Appr/Veto Memo"
                                  value={hasApVetoMemo}
                                  onChange={(e) => setHasApVetoMemo(e.target.checked)} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Is Substituted By"
                                  value={isSubstituted}
                                  onChange={(e) => setIsSubstituted(e.target.checked)} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Is Uni Bill"
                                  value={isUniBill}
                                  onChange={(e) => setIsUniBill(e.target.checked)} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Is Budget Bill"
                                  value={isBudgetBill}
                                  onChange={(e) => setIsBudgetBill(e.target.checked)} />
                </div>
                <div className={filterWrapperClass}>
                  <SearchCheckbox label="Is Rules Sponsored"
                                  value={isRulesSponsored}
                                  onChange={(e) => setIsRulesSponsored(e.target.checked)} />
                </div>
              </div>
            </div>


          </Accordion>
        </div>

        <div className="flex justify-end">
          <button className="btn my-3 w-36" type="submit" tabIndex="4">Search</button>
        </div>
      </form>
    </div>
  )
}

function SearchSelect({ label, value, onChange, options, tabindex }) {
  return (
    <label className="label label--top">{label}
      <select value={value}
              tabIndex={tabindex}
              onChange={onChange}
              className="select w-full">
        {options && options.map((opt) => <option value={opt.value} key={opt.value}>{opt.label}</option>)}
      </select>
    </label>
  )
}

function SearchTextInput({ label, value, onChange, placeholder }) {
  return (
    <label className="label label--top">{label}
      <input value={value}
             onChange={onChange}
             type="text"
             placeholder={placeholder}
             className="input w-full"
      />
    </label>
  )
}

function SearchCheckbox({ label, value, onChange }) {
  return (
    <React.Fragment>
      <input id={label}
             name={label}
             onChange={onChange}
             checked={value}
             type="checkbox"
             className="cursor-pointer"
      />
      <label htmlFor={label} className="label ml-2 cursor-pointer">{label}</label>
    </React.Fragment>
  )
}
