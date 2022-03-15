import React from 'react'
import {
  Link,
  useHistory,
  useLocation,
  useRouteMatch
} from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";
import * as queryString from "query-string";
import Accordion from "app/shared/Accordion";

export default function SectionAccordion({ section }) {
  const [ sectionText, setSectionText ] = React.useState()

  React.useEffect(() => {
    getLawsApi(section.lawId, section.locationId)
      .then((response) => {
        response.text = response.text.replaceAll("\\n", "\n")
        setSectionText(response.text)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`)
      })
  }, [ section ])

  if (!section) {
    return null
  }

  const title = (
    <div className="flex items-center text">
      <div className="w-24 lg:w-32 flex-none">
        § {section.locationId}
      </div>
      <div>
        <div>
          {section.title}
        </div>
        <div className="text text--small">
          Location Id: {section.locationId}
        </div>
      </div>
    </div>
  )

  const children = (
    <>
      <div className="text text--small my-3 ml-3 lg:ml-16 whitespace-pre-wrap">
        {sectionText}
      </div>
      <hr className="mb-3" />
    </>
  )
  return (
    <div>
      <Accordion title={title} children={children} type="laws" />
    </div>
  )

  // return (
  //   <div className="mt-8">
  //     <div className="pt-3">
  //       {response.documents.items.map(element => <Section results={element} term={term}
  //                                                         key={element.locationId} />)}
  //     </div>
  //   </div>
  // )

}


function Section({ results, term }) {
  const [ section, setSection ] = React.useState({ result: { items: [] } })
  const [ openText, setOpenText ] = React.useState(false)
  const location = useLocation()
  const history = useHistory()
  const params = queryString.parse(location.search, { parseBooleans: true })

  React.useEffect(() => {
    getLawsApi(results.lawId, results.locationId)
      .then((response) => {
        response.text = response.text.replaceAll("\\n", " ")
        setSection(response)
      })
      .catch((error) => {
        // TODO properly handle errors
        console.warn(`${error}`)
      })
  }, [ results ])

  return (
    <div>
      <div className="p-3 hover:bg-gray-200 flex flex-wrap"
           id={section.locationId}
           onClick={() => expandText({ openText, setOpenText })}>
        <div className="py-3 w-full">

          <div className="grid auto-rows-min grid-cols-3 ">

            <div className="text mr-5 row-start-1">
              <p>§&nbsp;{section.locationId}</p>
            </div>

            <div className="row-start-1 col-start-2">
              <p>Location&nbsp;Id:&nbsp;{section.locationId}</p>
              <p>{section.title}</p>
            </div>

            {(openText || term === section.locationId) &&
              <div className="text text--small col-start-1 col-span-3 mt-5">
                {section.text}
              </div>
            }

          </div>

        </div>
      </div>
    </div>
  )

  function expandText({ openText, setOpenText }) {
    setOpenText(!openText)
    // history.push({ search: queryString.stringify(params) +'#'+section.locationId })
  }
}

//may need other logic to handle expansion after pushing id of some kind to url