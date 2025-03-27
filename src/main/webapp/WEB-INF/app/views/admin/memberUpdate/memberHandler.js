import React from 'react';
import {useEffect, useState} from "react";
import Tabs from "app/shared/Tabs";
import UpdatePerson from "app/views/admin/memberUpdate/updatePerson";
import UpdateMember from "app/views/admin/memberUpdate/updateMember";
import UpdateSessionMember from "./updateSessionMember"

export default function MemberHandler({ setHeaderText }) {
  const tabs = [{name: "Update Person", component: <UpdatePerson/>}, {name:"Update Member",component: <UpdateMember/>}, {name: "Update Session Member",component:<UpdateSessionMember/>}
    ];
  const [activeTab, setActiveTab] = useState("Update Person");
  useEffect(() => {
    setHeaderText("Member Update")
  }, [])

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
