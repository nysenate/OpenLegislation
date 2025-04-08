import React from 'react';
import {useEffect, useState} from "react";
import Tabs from "app/shared/Tabs";
import {MemberData, PersonData, SessionData} from "app/views/admin/memberUpdate/MemberUpdateUtils";
import MemberUI from "app/views/admin/memberUpdate/MemberUI";

export default function MemberHandler({ setHeaderText }) {
  const tabs = [
    {name: "Update Person", component: <MemberUI fieldData={PersonData.fieldData} memberType= "Person" initialData={PersonData.initialdata}/> },
    {name:"Update Member",component: <MemberUI fieldData={MemberData.fieldData} memberType= "Member" initialData={MemberData.initialdata}/>},
    {name: "Update Session Member",component: <MemberUI fieldData={SessionData.fieldData} memberType= "Session" initialData={SessionData.initialData}/>}
    ];

  const [activeTab, setActiveTab] = useState("Update Person");

  return (
    <div>
      <div className="my-1">
        <Tabs tabs={tabs}
              activeTab={activeTab}
              setActiveTab={setActiveTab} />
      </div>
    </div>

  )
}
