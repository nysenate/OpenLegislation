import React from 'react'

export default function AgendaUpdatesForm({ updateValues, aWeekAgo, todaysDate }) {

  React.useEffect(() => {
    let agendaUpdateFrom = document.getElementById("updateFrom")
    agendaUpdateFrom.value = aWeekAgo
    let agendaUpdateTo = document.getElementById("updateTo")
    agendaUpdateTo.value = todaysDate
  }, [])

  // Updates the term query param when the form is submitted.
  const onSubmitAgendaUpdatesSearch = (e) => {
    e.preventDefault()

    const using = document.getElementById("using")
    console.log(using.value)

    const sortSelect = document.getElementById("sort")
    console.log(sortSelect.value)

    const showDetail = document.getElementById("showDetail")
    console.log(showDetail.checked)

    const updateFrom = document.getElementById("updateFrom")
    console.log(updateFrom.value)

    const updateTo = document.getElementById("updateTo")
    console.log(updateTo.value)

    updateValues(using.value, sortSelect.value, showDetail.checked,
      updateFrom.value, updateTo.value);
  }

  return (
    <div>

      <form onSubmit={onSubmitAgendaUpdatesSearch}>
        <div className="flex flex-wrap">
          <div className="flex-grow mr-8">

            <h1 className="p-5 font-bold">Show agenda updates during the following date range</h1>

            <div className="grid grid-flow-col grid-rows-2">

              <div className="row-start-1">
                <label htmlFor="updateFrom" className="label label--left uppercase font-bold p-2">From:</label>
                <input className="pl-4" type="date" id="updateFrom" name="updateFrom" tabIndex="1" />

                <label htmlFor="updateTo" className="label label--left uppercase font-bold p-2">To:</label>
                <input className="pl-4" type="date" id="updateTo" name="updateTo" tabIndex="2" />
              </div>

              <div className="row-start-2 gap-4">
                <label htmlFor="using" className="label label--left uppercase font-bold p-2">Using:</label>
                <select className="pl-4" name="using" id="using" tabIndex="3">
                  <option value="published">Published Date</option>
                  <option value="processed">Processed Date</option>
                </select>

                <label htmlFor="sort" className="label label--left uppercase font-bold p-2">Sort:</label>
                <select className="pl-4" name="sort" id="sort" tabIndex="4">
                  <option value="desc">Newest First</option>
                  <option value="asc">Oldest First</option>
                </select>

                <label htmlFor="sort" className="label label--left uppercase font-bold p-2">Show Detail:</label>
                <input type="checkbox" id="showDetail" name="showDetail" tabIndex="5" defaultChecked />
              </div>

            </div>

          </div>
        </div>

        <div className="flex justify-end">
          <button className="btn btn--primary my-3 w-36" type="submit" tabIndex="6">Search</button>
        </div>

      </form>


    </div>
  )
}