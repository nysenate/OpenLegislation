import React from 'react'

export default function CalendarBrowseForm({ updateValues }) {

  React.useEffect(() => {
  }, [])

  // Updates the term query param when the form is submitted.
  const onSubmitCalendarSearch = (e) => {
    e.preventDefault()

    const year = document.getElementById("year")
    console.log(year.value)

    const month = document.getElementById("month")
    console.log(month.value)

    updateValues(year.value, month.value)
  }

  return (
    <div>

      <form onSubmit={onSubmitCalendarSearch}>
        <div className="flex flex-wrap">
          <div className="flex-grow mr-8">

            <h1 className="p-5 font-bold">Browse Calendars</h1>

            <label htmlFor="year" className="label label--left uppercase font-bold p-2">Year:</label>
            <select className="pl-4" name="year" id="year" tabIndex="1">
              <option value="2021">2021</option>
              <option value="2022">2022</option>
              <option value="2020">2020</option>
              <option value="2019">2019</option>
              <option value="2018">2018</option>
              <option value="2017">2017</option>
              <option value="2016">2016</option>
              <option value="2015">2015</option>
              <option value="2014">2014</option>
              <option value="2013">2013</option>
              <option value="2012">2012</option>
              <option value="2011">2011</option>
              <option value="2010">2010</option>
              <option value="2009">2009</option>
            </select>

            <label htmlFor="sort" className="label label--left uppercase font-bold p-2">Month:</label>
            <select className="pl-4" name="month" id="month" tabIndex="4">
              <option value="1">January</option>
              <option value="2">February</option>
              <option value="3">March</option>
              <option value="4">April</option>
              <option value="5">May</option>
              <option value="6">June</option>
              <option value="7">July</option>
              <option value="8">August</option>
              <option value="9">September</option>
              <option value="10">October</option>
              <option value="11">November</option>
              <option value="12">December</option>
            </select>

          </div>
        </div>

        <div className="flex justify-end">
          <button className="btn my-3 w-36" type="submit" tabIndex="6">Search</button>
        </div>

      </form>


    </div>
  )
}