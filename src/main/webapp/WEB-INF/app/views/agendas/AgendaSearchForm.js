import React from 'react'
import Input from "app/shared/Input";
import Select, {
  SelectOption,
  sortOptions
} from "app/shared/Select";

export default function AgendaSearchForm({ updateValues, results }) {
  let resultsArray = results.result.items
  // console.log(resultsArray)

  function createYearOptions() {
    let yearOptions = []
    for (let i = new Date().getFullYear(); i >= 2009; i--) {
      yearOptions.push(new SelectOption(i,i))
    }
    return yearOptions
  }
  let yearOptions = createYearOptions()


  let sortOptions = [
    new SelectOption("desc", "Newest to Oldest"),
    new SelectOption("asc", "Oldest to Newest"),
    new SelectOption("", "Relevance")
  ]


  function createWeekOfOptions() {
    let weekOfOptions = []
    weekOfOptions.push(new SelectOption("any", "Any"))

    resultsArray.forEach( agendaInfo =>
    {
      // console.log(agendaInfo)
      weekOfOptions.push(new SelectOption(agendaInfo.weekOf, agendaInfo.weekOf))
    })
    return weekOfOptions
  }
  let weekOfOptions = createWeekOfOptions()


  function createAgendaNumberOptions() {
    let agendaNumberOptions = []
    agendaNumberOptions.push(new SelectOption("any", "Any"))

    resultsArray.forEach( agendaInfo =>
    {
      // console.log(agendaInfo.id.number)
      agendaNumberOptions.push(new SelectOption(agendaInfo.id.number, agendaInfo.id.number))
    })
    return agendaNumberOptions
  }
  let agendaNumberOptions = createAgendaNumberOptions()

  let committeeOptions = [
    new SelectOption("any", "Any"),
    new SelectOption("Aging", "Aging"),
    new SelectOption("Agriculture", "Agriculture"),
    new SelectOption("Alcoholism And Substance Abuse", "Alcoholism And Substance Abuse"),
    new SelectOption("Banks", "Banks"),
    new SelectOption("Budget And Revenue", "Budget And Revenue"),
    new SelectOption("Children And Families", "Children And Families"),
    new SelectOption("Cities", "Cities"),
    new SelectOption("Civil Service And Pensions", "Civil Service And Pensions"),
    new SelectOption("Codes", "Codes"),
    new SelectOption("Commerce, Economic Development And Small Business", "Commerce, Economic Development And Small Business"),
    new SelectOption("Consumer Protection", "Consumer Protection"),
    new SelectOption("Corporations, Authorities And Commissions", "Corporations, Authorities And Commissions"),
    new SelectOption("Crime Victims, Crime And Correction", "Crime Victims, Crime And Correction"),
    new SelectOption("Cultural Affairs, Tourism, Parks And Recreation", "Cultural Affairs, Tourism, Parks And Recreation"),
    new SelectOption("Domestic Animal Welfare", "Domestic Animal Welfare"),
    new SelectOption("Education", "Education"),
    new SelectOption("Elections", "Elections"),
    new SelectOption("Energy And Telecommunications", "Energy And Telecommunications"),
    new SelectOption("Ethics And Internal Governance", "Ethics And Internal Governance"),
    new SelectOption("Finance", "Finance"),
    new SelectOption("Health", "Health"),
    new SelectOption("Higher Education", "Higher Education"),
    new SelectOption("Housing, Construction And Community Development", "Housing, Construction And Community Development"),
    new SelectOption("Insurance", "Insurance"),
    new SelectOption("Internet And Technology", "Internet And Technology"),
    new SelectOption("Investigations And Government Operations", "Investigations And Government Operations"),
    new SelectOption("Judiciary", "Judiciary"),
    new SelectOption("Labor", "Labor"),
    new SelectOption("Local Government", "Local Government"),
    new SelectOption("Mental Health And Development Disabilities", "Mental Health And Development Disabilities"),
    new SelectOption("New York City Education", "New York City Education"),
    new SelectOption("Racing, Gaming And Wagering", "Racing, Gaming And Wagering"),
    new SelectOption("Rules", "Rules"),
    new SelectOption("Social Services", "Social Services"),
    new SelectOption("Transportation", "Transportation"),
    new SelectOption("Veterans, Homeland Security And Military Affairs", "Veterans, Homeland Security And Military Affairs"),
    new SelectOption("Women's Issues", "Women's Issues")
  ]


  React.useEffect(() => {
  }, [])

  // Updates the term query param when the form is submitted.
  const onSubmitAgendaSearch = (e) => {
    e.preventDefault()

    const year = document.getElementById("yearSelect")
    // console.log(year.value)

    const sortSelect = document.getElementById("sortSelect")
    // console.log(sortSelect.value)

    const weekOfSelect = document.getElementById("weekOfSelect")
    // console.log(weekOfSelect.value)

    const agendaNumberSelect = document.getElementById("agendaNumberSelect")
    // console.log(agendaNumberSelect.value)

    const committeeSelect = document.getElementById("committeeSelect")
    // console.log(committeeSelect.value)

    const baseBillNoInput = document.getElementById("baseBillNo")
    // console.log(baseBillNoInput.value)

    const meetingNotes = document.getElementById("meetingNotes")
    // console.log(meetingNotes.value)

    updateValues(year.value, sortSelect.value, weekOfSelect.value, agendaNumberSelect.value, committeeSelect.value, baseBillNoInput.value, meetingNotes.value)
  }

  function resetFilters() {
    const baseBillNoInput = document.getElementById("baseBillNo")
    baseBillNoInput.value = ""

    const meetingNotes = document.getElementById("meetingNotes")
    baseBillNoInput.value = ""

  }

  return (

    <div>

      <form onSubmit={onSubmitAgendaSearch}>
        <div className="flex flex-wrap">

          <h1 className="font-bold">Agenda Search</h1>

          <div className="flex-grow flex justify-between p-10">

            <Select label="Year:" options={yearOptions} name="yearSelect" tabIndex="1"/>

            <Select label="Sort by" options={sortOptions} name="sortSelect" tabIndex="2"/>

            <Select label="Week Of:" options={weekOfOptions} name="weekOfSelect" tabIndex="3" />

            <Select label="Agenda No.:" options={agendaNumberOptions} name="agendaNumberSelect" tabIndex="4" />

            <Select label="Committee:" options={committeeOptions} name="committeeSelect" tabIndex="5" />

            <Input label="Base Bill No:" placeholder="e.g. S1234" name="baseBillNo" tabIndex="6" />

            <Input label="Meeting Notes:" placeholder="e.g. Off the floor" name="meetingNotes" tabIndex="7" />

            <button className="btn my-3 w-36" onClick={resetFilters} tabIndex="9">Search</button>

          </div>
        </div>

        <div className="flex justify-end">
          <button className="btn btn--primary my-3 w-36" type="submit" tabIndex="10">Search</button>
        </div>

      </form>

    </div>
  )
}