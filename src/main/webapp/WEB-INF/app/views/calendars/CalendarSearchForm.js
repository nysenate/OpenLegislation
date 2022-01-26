import React from 'react'
import Input from "app/shared/Input";
import Select, {
  SelectOption,
  sortOptions
} from "app/shared/Select";

export default function CalendarSearchForm({ updateValues }) {

  const yearOptions = [
    new SelectOption("2022", "2022"),
    new SelectOption("2021", "2021"),
    new SelectOption("2020", "2020"),
    new SelectOption("2019", "2019"),
    new SelectOption("2018", "2018"),
    new SelectOption("2017", "2017"),
    new SelectOption("2016", "2016"),
    new SelectOption("2015", "2015"),
    new SelectOption("2014", "2014"),
    new SelectOption("2013", "2013"),
    new SelectOption("2012", "2012"),
    new SelectOption("2011", "2011"),
    new SelectOption("2010", "2010"),
    new SelectOption("2009", "2009"),
  ]

  const withOptions = [
    new SelectOption("calendarNumber", "Calendar No."),
    new SelectOption("printNo", "Print No."),
    new SelectOption("billCalNo", "Bill Calendar No.")
  ]

  React.useEffect(() => {
  }, [])

  // Updates the term query param when the form is submitted.
  const onSubmitCalendarSearch = (e) => {
    e.preventDefault()

    const year = document.getElementById("yearSelect")
    console.log(year.value)

    const searchBy = document.getElementById("withSelect")
    console.log(searchBy.value)

    const searchValue = document.getElementById("searchValue")
    console.log(searchValue.value)

    const sortSelect = document.getElementById("sortSelect")
    console.log(sortSelect.value)

    const activeListOnly = document.getElementById("activeListOnly")
    console.log(activeListOnly.checked)

    updateValues(year.value, searchBy.value, searchValue.value, sortSelect.value, activeListOnly.checked)
  }

  return (

    <div>

      <form onSubmit={onSubmitCalendarSearch}>
        <div className="flex flex-wrap">

          <h1 className="font-bold">Field Search</h1>

          <div className="flex-grow flex justify-between p-10">

            <Select label="Year:" options={yearOptions} name="yearSelect" tabIndex="1"/>

            <Select label="With:" options={withOptions} name="withSelect" tabIndex="2" />

            <Input label="Search Value:" placeholder="1" name="searchValue" tabIndex="3" />

            <Select label="Sort by" options={sortOptions} name="sortSelect" tabIndex="4"/>

            <Input label="Active List Only:" type="checkbox"
                   name="activeListOnly" tabIndex="5"/>

          </div>
        </div>

        <div className="flex justify-end">
          <button className="btn my-3 w-36" type="submit" tabIndex="6">Search</button>
        </div>

      </form>

    </div>
  )
}