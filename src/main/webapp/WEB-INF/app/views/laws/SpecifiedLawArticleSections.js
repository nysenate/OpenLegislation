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
    getLawsApi(results.lawId, results.locationId)
        .then((response) => {
            setSection(response)
        })
        .catch((error) => {
            // TODO properly handle errors
            console.warn(`${error}`)
        })

    // console.log(section)

    return (
        // <div>
        //     TEST
        // </div>
        <div>
             {/*<Link to={`/laws/${documents.lawId}?location=${documents.locationId}`} key={documents.lawId}>*/}
            <div className="p-3 hover:bg-gray-200 flex flex-wrap">
                <div className="flex items-center w-full md:w-1/3">
                    <div>

                        <div className="text">
                            § {section.locationId}
                        </div>

                        <div className="text text--small">
                            Location Id: {section.locationId}
                            {section.title}
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