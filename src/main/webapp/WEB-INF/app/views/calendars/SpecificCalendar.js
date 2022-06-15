import React from 'react';
import {
  useParams
} from "react-router-dom";
import { fetchCalendar } from "app/apis/calendarApi";
import { DateTime } from "luxon";
import { formatDateTime } from "app/lib/dateUtils";
import Tabs from "app/shared/Tabs";
import CalendarSectionList from "app/views/calendars/CalendarSectionList";
import LoadingIndicator from "app/shared/LoadingIndicator";
import ErrorMessage from "app/shared/ErrorMessage";


export default function SpecificCalendar({ setHeaderText }) {
  const [ response, setResponse ] = React.useState()
  const [ sectionMap, setSectionMap ] = React.useState(new Map())
  const [ loading, setLoading ] = React.useState(true)
  const [ errorMsg, setErrorMsg ] = React.useState("")
  const [ tabs, setTabs ] = React.useState([])
  const [ activeTab, setActiveTab ] = React.useState("")
  const { year, number } = useParams()

  React.useEffect(() => {
    getCalendar(year, number)
  }, [ year, number ])

  React.useEffect(() => {
    if (response) {
      const calDate = formatDateTime(DateTime.fromISO(response.result.calDate), DateTime.DATE_MED)
      setHeaderText(`Senate Calendar #${response.result.calendarNumber} - ${calDate}`)
      setSectionMap(new Map([ ...createFloorSectionMap(response), ...createActiveListSectionMap(response) ]))
    }
  }, [ response ])

  React.useEffect(() => {
    const tabs = []
    for (const type of SECTION_TYPES) {
      if (sectionMap.get(type.name)) {
        const section = sectionMap.get(type.name)
        tabs.push({
          name: type.label,
          quantity: section.supplementals.size,
          isDisable: false,
          component: <CalendarSectionList section={section} />
        })
      }
    }
    setTabs(tabs)
  }, [ sectionMap ])

  React.useEffect(() => {
    if (tabs.length) {
      setActiveTab(tabs[0].name)
    }
  }, [ tabs ])

  const getCalendar = (year, number) => {
    setLoading(true)
    setResponse(null)
    setErrorMsg("")
    setHeaderText(`Senate Calendar #${number}`)
    fetchCalendar(year, number)
      .then(res => setResponse(res))
      .catch((error) => setErrorMsg(error.message))
      .finally(() => setLoading(false))
  }

  return (
    <div className="mt-6 mb-3">
      {loading &&
        <LoadingIndicator />
      }
      {!loading && errorMsg &&
        <ErrorMessage>{errorMsg}</ErrorMessage>
      }
      {!loading && !errorMsg &&
        <Tabs tabs={tabs}
              activeTab={activeTab}
              setActiveTab={tab => setActiveTab(tab)} />
      }
    </div>
  )
}

/**
 * A list of all possible sections and their labels in the order they should be displayed.
 */
const SECTION_TYPES = [
  new CalendarSection("ACTIVE_LIST", "Active List"),
  new CalendarSection("STARRED_ON_THIRD_READING", "Starred on Third Reading"),
  new CalendarSection("THIRD_READING_FROM_SPECIAL_REPORT", "Third Reading from Special Report"),
  new CalendarSection("THIRD_READING", "Third Reading"),
  new CalendarSection("ORDER_OF_THE_SPECIAL_REPORT", "Special Report"),
  new CalendarSection("ORDER_OF_THE_SECOND_REPORT", "Second Report"),
  new CalendarSection("ORDER_OF_THE_FIRST_REPORT", "First Report"),
  new CalendarSection("RESOLUTIONS", "Resolutions"),
]

function CalendarSection(name, label) {
  this.name = name
  this.label = label
}

/**
 * @param suppId {string} The String identifying the supplemental, i.e. "A", "B", etc.
 * For the floor calendar original use "ORIGINAL".
 * @param entries {Array} An array of entries belonging to this supplemental id. Used here, each instance of this
 * will only have the entries for a single section type of this supplemental.
 * @constructor
 */
function Supplemental(suppId, releaseDateTime, entries) {
  this.suppId = suppId
  this.releaseDateTime = releaseDateTime
  this.entries = entries
}

/**
 * @param sectionType {string} The CalendarSectionType which all Supplementals contained within belong to.
 *                             i.e. "THIRD_READING". For the purposes of this UI, "Active List" is also used as a section type.
 * @param supplementals {Map.<suppId, Supplemental>} An map of the supplemental id to the supplemental.
 * @constructor
 */
function Section(sectionType, supplementals = new Map()) {
  this.sectionType = sectionType
  this.supplementals = supplementals
}

function createFloorSectionMap(response) {
  // Combine floorCalendar and supplementalCalendars into the same format.
  const floorCalendars = {}
  floorCalendars["ORIGINAL"] = response.result.floorCalendar
  Object.keys(response.result.supplementalCalendars.items).forEach(suppId => {
    floorCalendars[suppId] = response.result.supplementalCalendars.items[suppId]
  })

  const sectionsMap = new Map()
  Object.keys(floorCalendars).forEach(suppId => {
    const suppReleaseDateTime = floorCalendars[suppId].releaseDateTime
    const entriesBySection = floorCalendars[suppId].entriesBySection.items
    Object.keys(entriesBySection).forEach(sectionName => {
      const section = entriesBySection[sectionName]
      const floorSupp = new Supplemental(suppId, suppReleaseDateTime, section.items)
      if (!sectionsMap.get(sectionName)) {
        sectionsMap.set(sectionName, new Section(sectionName))
      }
      sectionsMap.get(sectionName).supplementals.set(floorSupp.suppId, floorSupp)
    })
  })
  return sectionsMap
}

function createActiveListSectionMap(response) {
  const AL_SECTION = "ACTIVE_LIST"
  const alSectionMap = new Map()
  alSectionMap.set(AL_SECTION, new Section(AL_SECTION))
  const activeListSupplementals = response.result.activeLists.items
  Object.keys(activeListSupplementals).forEach(suppId => {
    const supplemental = activeListSupplementals[suppId]
    alSectionMap.get(AL_SECTION).supplementals.set(
      suppId,
      new Supplemental(suppId, supplemental.releaseDateTime, supplemental.entries.items))
  })
  return alSectionMap
}
