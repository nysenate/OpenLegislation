import React from 'react';
import {useState} from "react";
import Tabs from "app/shared/Tabs";
import {MemberData, PersonData, SessionData} from "app/views/admin/memberUpdate/MemberUpdateUtils";
import MemberUI from "app/views/admin/memberUpdate/MemberUI";

export default function MemberHandler({ setHeaderText }) {
  const tabs = [
    {name: "Person", component: <MemberUI fieldData={PersonData.fieldData} memberType="Person" initialData={PersonData.initialData}/> },
    {name: "Member", component: <MemberUI fieldData={MemberData.fieldData} memberType="Member" initialData={MemberData.initialData}/>},
    {name: "Session Member", component: <MemberUI fieldData={SessionData.fieldData} memberType="Session Member" initialData={SessionData.initialData}/>}
  ];

  React.useEffect(() => {
    setHeaderText("Manage Members")
  }, [])
  const [activeTab, setActiveTab] = useState("Person");

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
