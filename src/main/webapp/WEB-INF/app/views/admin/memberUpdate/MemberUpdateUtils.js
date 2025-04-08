import { yearSortOptions } from "app/shared/Select";

export const handleUpdateMember = async (tableName, operation, formData, feildData) => {
  let payload = {}
  feildData[operation].forEach(field => {
    if (formData[field.fieldName] !== undefined) {
      payload[field.fieldName] = formData[field.fieldName];
    }
  });
  if (operation === 'update') {
    payload = { id: formData.id, ...payload };
  } else if (operation === 'delete') {
    payload = { id: formData.id };
  }
  const api = `/api/3/members/${tableName.toUpperCase()}?action=${operation.toUpperCase()}`
  const myHeaders = new Headers()
  myHeaders.append("Content-Type", "application/json")
  const myRequest = new Request(api, {
    method: 'PUT',
    body: JSON.stringify(payload),
    headers: myHeaders
  })

  try {
    const results = await fetch(myRequest)
    return results.json();
  } catch (error) {
    return new Error(error);
  }

}

export const MemberData = {
  initialdata: {
    operation: 'create',
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
        options: [ { value: "Senate", label: "Senate" }, { value: "Assembly", label: "Assembly" } ],
        fieldName: 'chamber'
      },
    ],
    update: [
      {
        label: 'Member ID',
        type: 'input',
        fieldName: 'id',
        required: true
      },
      {
        label: 'Incumbent',
        type: 'select',
        options: [ { value: "true", label: "True" }, { value: "false", label: "False" } ],
        fieldName: 'incumbent'
      },
    ],
    delete: [
      {
        label: 'Member ID',
        type: 'input',
        fieldName: 'id',
        required: true
      },
    ]
  }
}
export const PersonData = {
  fieldData: {
    create: [
      {
        label: 'First Name',
        type: 'input',
        fieldName: 'firstName',
        required: true
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
        required: true
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
      },
    ],
    update: [
      {
        label: 'Person Id',
        type: 'input',
        fieldName: 'id',
        required: true
      },
      {
        label: 'First Name',
        type: 'input',
        fieldName: 'firstName'
      },
      {
        label: 'Middle Name',
        type: 'input',
        fieldName: 'middleName'
      },
      {
        label: 'Last Name',
        type: 'input',
        fieldName: 'lastName'
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
      },
    ],
    delete: [
      {
        label: 'Person Id',
        type: 'input',
        fieldName: 'id',
        required: true
      },
    ]
  },
  initialdata: {
    operation: 'create',
  }
}

const sessionYearOptions = yearSortOptions(2009, false, true);
export const SessionData = {
  initialData: {
    alternate: true,
    operation: 'create',
    sessionYear:sessionYearOptions[0].value,
  },
  fieldData: {
    create: [
      {
        label: 'Member Id',
        type: 'input',
        fieldName: 'memberId',
        required: true
      },
      {
        label: 'Session Year',
        type: 'select',
        options: sessionYearOptions,
        fieldName: 'sessionYear',
        required: true
      },
      {
        label: 'Lbdc Short Name',
        type: 'input',
        fieldName: 'lbdcShortName',
        required: true
      },
      {
        label: 'District Code',
        type: 'input',
        fieldName: 'districtCode',
        required: true
      },
    ],
    update: [
      {
        label: 'Session Member Id',
        type: 'input',
        fieldName: 'id',
        required: true
      },
      {
        label: 'Alternate',
        type: 'select',
        options: [ { value: "true", label: "True" }, { value: "false", label: "False" } ],
        fieldName: 'alternate'
      },
      {
        label: 'District Code',
        type: 'input',
        fieldName: 'districtCode'
      },
    ],
    delete: [
      {
        label: 'Session Member Id',
        type: 'input',
        fieldName: 'id',
        required: true
      },
    ]
  }
}