import React from 'react'
import {Link} from "react-router-dom";
import getLawsApi from "app/apis/getLawsApi";

export default function SpecifiedLawArticleSections({response}) {

    // console.log(response)
    return (
        <div className="mt-8">
            <div className="pt-3">
                {response.documents.items.map(element => <Section results={element} key={element.locationId}/>)}
            </div>
        </div>
    )

}


function Section({results}) {
    // console.log(results)
    const [section, setSection] = React.useState({result: {items: []}})

    React.useEffect(() => {
      getLawsApi(results.lawId, results.locationId)
        .then((response) => {
          setSection(response)
        })
        .catch((error) => {
          // TODO properly handle errors
          console.warn(`${error}`)
        })
    }, [results])

    return (
        <div>
             {/*<Link to={`/laws/${documents.lawId}?location=${documents.locationId}`} key={documents.lawId}>*/}
            <div className="p-3 hover:bg-gray-200 flex flex-wrap">
                <div className="py-3 w-full md:w-1/3">
                    <div className="flex items-center">

                        <div className="text mr-5">
                            <p>§&nbsp;{section.locationId}</p>
                        </div>

                        <div>
                            <div className="text text--small">
                                <p>Location&nbsp;Id:&nbsp;{section.locationId}</p>
                                <p>{section.title}</p>
                            </div>
                        </div>


                        {/*<div className="text text--small">*/}
                        {/*    {section.text}*/}
                        {/*</div>*/}
                    </div>
                </div>
            </div>
            {/*</Link>*/}
        </div>
)
}