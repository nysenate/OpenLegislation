import { yearSortOptions } from "app/shared/Select";

export const handleUpdateMember = async (tableName, operation, formData, fieldData) => {
  let payload = {}
  fieldData[operation].forEach(field => {
    if (formData[field.fieldName] !== undefined) {
      payload[field.fieldName] = formData[field.fieldName];
    }
  });
  if (operation === 'update') {
    payload = { id: formData.id, ...payload };
  } else if (operation === 'delete') {
    payload = { id: formData.id };
  }
  const api = `/api/3/members/${tableName.toUpperCase()}/${operation.toUpperCase()}`
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

const memberIdInput = {
  label: 'Member ID',
  type: 'input',
  fieldName: 'id',
  required: true
};
export const MemberData = {
  initialData: {
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
  fieldName: 'id',
  required: true
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
  }
}

const sessionMemberIdInput = {
  label: 'Session Member ID',
  type: 'input',
  fieldName: 'id',
  required: true
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
    alternate: true,
    operation: 'create',
    sessionYear:sessionYearOptions[0].value,
  },
  fieldData: {
    create: [
      {
        label: 'Member ID',
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
        label: 'LBDC Short Name',
        type: 'input',
        fieldName: 'lbdcShortName',
        required: true
      },
      districtCodeInput(true)
    ],
    update: [
      sessionMemberIdInput,
      {
        label: 'Alternate',
        type: 'select',
        options: [ { value: "true", label: "True" }, { value: "false", label: "False" } ],
        fieldName: 'alternate'
      },
      districtCodeInput(false)
    ],
    delete: [ sessionMemberIdInput ]
  }
}
