import { yearSortOptions } from "app/shared/Select";

export const chamberOptions = [ { value: "Senate", label: "Senate" }, { value: "Assembly", label: "Assembly" } ];

export const handleUpdateMember = async (tableName, operation, formData, fieldData) => {
  let modelMap = {};
  let updatedAttributes = [];
  fieldData[operation].forEach(field => {
    if (formData[field.fieldName] !== undefined) {
      modelMap[field.fieldName] = formData[field.fieldName];
    }
  });
  if (operation === 'update') {
    modelMap = { id: formData.id,
      ...modelMap };
  } else if (operation === 'delete') {
    modelMap = { id: formData.id };
  }
  const api = `/api/3/members/${tableName.toUpperCase()}/${operation.toUpperCase()}`
  const myHeaders = new Headers()
  myHeaders.append("Content-Type", "application/json")
  const myRequest = new Request(api, {
    method: 'PUT',
    body: JSON.stringify({
      modelMap: modelMap,
      updatedAttributes: updatedAttributes
    }),
    headers: myHeaders
  })

  try {
    const results = await fetch(myRequest)
    return results.json();
  } catch (error) {
    return new Error(error);
  }
}

export const fetchPersons = async (lastName) => {
  const api = `/api/3/members/search?term=personView.lastName=${lastName}&full=true`;

  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const myRequest = new Request(api, {
    method: 'GET',
    headers: myHeaders,
  });
  try {
    const results = await fetch(myRequest);
    if (!results.ok) {
      throw new Error(`HTTP error! status: ${results.status}`);
    }
    const data = await results.json();
    const personsList = data?.result?.items || [];
    return personsList
  } catch (error) {
    console.error('Failed to fetch persons:', error);
  }
};

const memberIdInput = {
  label: 'Member ID',
  type: 'input',
  fieldName: 'memberId',
  display: false,
};
export const MemberData = {
  initialData: {
    operation: 'create',
    memberId: undefined,
    personId: undefined,
    incumbent: true,
    chamber: "Senate"
  },
  fieldData: {
    create: [
      {
        label: 'Person ID',
        type: 'input',
        fieldName: 'personId',
        required: true
      },
      {
        label: 'Chamber',
        type: 'select',
        options: chamberOptions,
        fieldName: 'chamber'
      },
      {
        label:'Incumbent',
        type:'input',
        fieldName: 'incumbent',
        disabled: true,
      }
    ],
    update: [
      memberIdInput,
      {
        label: 'Incumbent',
        type: 'select',
        options: [ { value: "true", label: "True" }, { value: "false", label: "False" } ],
        fieldName: 'incumbent'
      },
    ],
    delete: [ memberIdInput ]
  }
}

const personIdInput = {
  label: 'Person ID',
  type: 'input',
  fieldName: 'personId',
  display: false,
};
const personDataFields = function(requireName) {
  return [
    {
      label: 'First Name',
      type: 'input',
      fieldName: 'firstName',
      required: requireName
    },
    {
      label: 'Middle Name',
      type: 'input',
      fieldName: 'middleName'
    },
    {
      label: 'Last Name',
      type: 'input',
      fieldName: 'lastName',
      required: requireName
    },
    {
      label: 'Suffix',
      type: 'input',
      fieldName: 'suffix'
    },
    {
      label: 'Email Address',
      type: 'input',
      fieldName: 'email'
    },
    {
      label: 'Profile Picture',
      type: 'input',
      fieldName: 'imgName'
    }
  ]
}
export const PersonData = {
  fieldData: {
    create: personDataFields(true),
    update: [
      personIdInput, ...personDataFields(false)],
    delete: [ personIdInput ]
  },
  initialData: {
    operation: 'create',
    personId:undefined,
    firstName: undefined,
    middleName:undefined,
    lastName:undefined,
    suffix:undefined,
    email:undefined,
    imgName:undefined,

  }
}

const sessionMemberIdInput = {
  label: 'Session Member ID',
  type: 'input',
  fieldName: 'sessionMemberId',
  display: false,
};
const districtCodeInput = function(required) {
  return {
    label: 'District Code',
    type: 'input',
    fieldName: 'districtCode',
    required: required
  }
}
const sessionYearOptions = yearSortOptions(2009, false, true);
export const SessionData = {
  initialData: {
    sessionMemberId:undefined,
    memberId: undefined,
    alternate: true,
    lbdcShortName:undefined,
    districtCode:undefined,
    operation: 'create',
    sessionYear:sessionYearOptions[0].value,
  },
  fieldData: {
    create: [
      {
        label: 'Member ID',
        type: 'input',
        fieldName: 'memberId',
        display: false,
      },
      {
        label: 'Session Year',
        type: 'select',
        options: sessionYearOptions,
        fieldName: 'sessionYear',
        required: true
      },
      {
        label: 'LBDC Short Name',
        type: 'input',
        fieldName: 'lbdcShortName',
        required: true
      },
      districtCodeInput(true),
      {
        label: 'Alternate',
        type: 'input',
        fieldName: 'alternate',
        disabled:true
      }
    ],
    update: [
      sessionMemberIdInput,
      {
        label: 'Alternate',
        type: 'select',
        options: [ { value: "true", label: "True" }, { value: "false", label: "False" } ],
        fieldName: 'alternate'
      },
      districtCodeInput(false),
      {
        label: 'Member ID',
        type: 'input',
        fieldName: 'memberId',
        display: false,
      },
      {
        label: 'LBDC Short Name',
        type: 'input',
        fieldName: 'lbdcShortName',
        required: true,
        disabled:true,
      },
      {
        label: 'Session Year',
        type: 'select',
        options: sessionYearOptions,
        fieldName: 'sessionYear',
        required: true,
        disabled: true,
      },
    ],
    delete: [ sessionMemberIdInput ]
  }
}
