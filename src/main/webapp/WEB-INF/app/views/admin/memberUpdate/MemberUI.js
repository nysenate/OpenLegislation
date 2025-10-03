import React, {
  useState,
  useEffect,
  useMemo
} from "react";
import Select from "app/shared/Select";
import {
  handleUpdateMember,
  fetchPersons,
  chamberOptions
} from "app/views/admin/memberUpdate/MemberUpdateUtils";
import ErrorMessage from "app/shared/ErrorMessage";
import Modal from "app/shared/Modal";

const MemberUI = ({ initialData, memberType, fieldData }) => {
  const [ formData, setFormData ] = useState(initialData);
  const [ submitSuccess, setSubmitSuccess ] = useState(null);
  const [ filterByLastName, setFilterByLastName ] = useState(undefined)
  const [ filterByPerson, setFilterByPerson ] = useState(undefined)
  const [ persons, setPersons ] = useState([])
  const [fullPersons, setFullPersons] = useState([]);
  const [ filterByChamber, setFilterByChamber ] = useState("")
  const [ selectedSessionYear, setSelectedSessionYear ] = useState()
  const [originalData, setOriginalData] = useState(initialData);

  useEffect(() => {
    setFormData({ ...initialData });
    setFilterByLastName(undefined);
    setFilterByPerson(undefined);
    setSelectedSessionYear(undefined);
    setPersons([]);
    setFullPersons([]);
    setFilterByChamber("");
  }, [ initialData ]);

  //Filtering distinct persons with personId
  const uniquePersons = useMemo(() => {
    const seen = new Set();
    return persons.filter((p) => {
      const id = p.person?.personId;
      if (!id || seen.has(id)) return false;
      seen.add(id);
      return true;
    });
  }, [ persons ]);

  //Fetch Persons with entered last name
  useEffect(async () => {
    const loadPersons = async () => {
      const data = await fetchPersons(filterByLastName);
      setFullPersons(data);
      setPersons(data);
    };
    if (filterByLastName !== undefined) {
      loadPersons();
    }
  }, [ filterByLastName ]);

  useEffect(() => {
    if (filterByPerson !== undefined) {
      const newData = { ...formData };
      Object.keys(formData).forEach((key) => {
        if(key === 'alternate') {
           newData[key] = true;
        }
        else if (key === 'lbdcShortName' && 'shortName' in filterByPerson) {
          newData[key] = filterByPerson.shortName;
        } else if (filterByPerson.person && key in filterByPerson.person) {
          newData[key] = filterByPerson.person[key];
        } else if (key in filterByPerson) {
          newData[key] = filterByPerson[key];
        }
      });
      setFormData(newData);
      setOriginalData(newData);
    }
  }, [ filterByPerson ]);

  useEffect(() => {
    if (filterByChamber === "") {
      setPersons(fullPersons);
    } else {
      const filtered = fullPersons.filter(
        (person) => person.chamber === filterByChamber.toUpperCase()
      );
      setPersons(filtered);
      setFilterByPerson(filtered[0])
    }
  }, [ filterByChamber ]);

  useEffect(() => {
    if (filterByPerson?.sessionShortNameMap && selectedSessionYear) {
      const id = filterByPerson.sessionShortNameMap[selectedSessionYear]?.[0]?.sessionMemberId;
      setFormData({
        ...formData,
        sessionYear: selectedSessionYear,
        sessionMemberId: id
      });
    }
  }, [ selectedSessionYear ]);


  const showFilterByLastName = !(memberType === 'Person' && formData.operation === 'create')

  const showFilterByPerson = filterByLastName && showFilterByLastName

  const availableChamberOptions = useMemo(() => {
    if (!filterByPerson?.person?.personId) return [];
    const personId = filterByPerson.person.personId;
    const matchingChambers = [
      ...new Set(
        persons
          .filter(p => p.person?.personId === personId)
          .map(p => p.chamber.toUpperCase())
      )
    ];
    return chamberOptions.filter(option =>
      matchingChambers.includes(option.value.toUpperCase())
    );
  }, [filterByPerson, filterByChamber]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const confirmed = window.confirm("Are you sure you want to submit?");
    if (!confirmed) return;

    // Get hidden and required fields
    const hiddenFields = fieldData[initialData.operation]?.filter(
      (f) => f.display === false && f.required === true
    ) || [];

    // Check if any required hidden field is missing
    for (const field of hiddenFields) {
      if (
        formData[field.fieldName] == null ||
        formData[field.fieldName] === ""
      ) {
        setFormData((prev) => ({
          ...prev,
          responseMessage: `Required hidden field "${field.label}" is missing.`,
        }));
        setSubmitSuccess(false);
        return;
      }
    }
    let updatedAttributes = [];
    if (formData.operation === 'update') {
      updatedAttributes = fieldData.update
        .filter(field => field.fieldName in formData)
        .filter(field => {
          const fieldName = field.fieldName;
          return formData[fieldName] !== originalData[fieldName];
        })
        .map(field => field.fieldName);
    }

    try {
      const response = await handleUpdateMember(
        memberType.replaceAll(" ", "_"),
        formData.operation,
        formData,
        fieldData,
        updatedAttributes
      );

      setSubmitSuccess(response.success);
      setFormData((prevData) => ({
        ...prevData,
        responseMessage: response.message,
      }));
    } catch (error) {
      setSubmitSuccess(false);
      setFormData((prevData) => ({
        ...prevData,
        responseMessage: "Submission failed. Please try again.",
      }));
    }
  };


  const closePopup = () => {
    setFormData(initialData);
    setSubmitSuccess(null)
  };

  return <div className="p-3">
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="flex justify-end">
        <label className="label label--top">Choose Operation:</label>
        <select
          id="operation"
          value={formData.operation}
          onChange={(e) => setFormData({ ...formData, operation: e.target.value })}
          className="select block ml-2"
        >
          <option value="create">Create</option>
          <option value="update">Update</option>
          <option value="delete">Delete</option>
        </select>
      </div>
      {showFilterByLastName && <div className="flex flex-col gap-x-6 gap-y-3 justify-center border m-5 p-3">
        <div>
          <p>Filter By:</p>
        </div>
        <div className="flex flex-row gap-x-6 gap-y-3">
          {showFilterByLastName && <div className="flex flex-col">
            <label className="label label--top">Last Name:</label>
            <input
              type="text"
              value={filterByLastName !== undefined ? filterByLastName : ''}
              onChange={(e) => {
                setFilterByLastName(e.target.value);
                setFilterByPerson(undefined);
                setFilterByChamber("");
                setSelectedSessionYear(undefined);
              }}
              className="input block w-52 text-sm"
            />
          </div>}
          {showFilterByPerson && <div className="flex flex-col">
            <label className="label label">Person:</label>
            <select
              value={filterByPerson?.memberId || ''}
              onChange={(e) => {
                const selectedId = Number(e.target.value);
                if (!selectedId) {
                  setFilterByPerson(undefined);
                  setFilterByChamber("");
                  setSelectedSessionYear(undefined);
                  setPersons(fullPersons);
                } else {
                  const selectedPerson = persons?.find(p => p.memberId === selectedId);
                  setFilterByPerson(selectedPerson);
                }
              }}
              className="input w-52 text-sm"
            >
              <option value="">Select Person</option>
              {uniquePersons?.map(person => (
                <option key={person.memberId} value={person.memberId}>
                  {person.person?.fullName || 'Unknown'}
                </option>
              ))}
            </select>
          </div>}
          {filterByPerson && (memberType !== 'Person' &&  !( memberType ==='Member' && formData.operation === 'Create')) && <div>
            <label className="label label--top">Chamber:</label>
            <Select
              value={filterByChamber}
              options={[ { value: "", label: "Select Chamber" }, ...availableChamberOptions ]}
              onChange={(e) => {
                const value = e.target.value;
                setFilterByChamber(value);
                if (value === "") {
                  setSelectedSessionYear(undefined);
                }
              }}
              className="input w-52 text-sm"
            />
          </div>}
          {filterByChamber && (memberType === 'Session Member' && formData.operation !== 'create') &&
            <div className="flex flex-col">
              <label className="label label--top">Session Member:</label>
              <select
                value={filterByPerson ? JSON.stringify({
                  memberId: filterByPerson.memberId,
                  sessionYear: filterByPerson.sessionYear
                }) : ''}
                onChange={(e) => {
                  if (e.target.value === "") {
                    setSelectedSessionYear(undefined);
                    return;
                  }
                  const selectedValue = JSON.parse(e.target.value);
                  const selectedPerson = persons.find(
                    (p) => p.memberId === selectedValue.memberId
                  );
                  setSelectedSessionYear(selectedValue.sessionYear);
                  setFilterByPerson({
                    ...selectedPerson,
                    sessionYear: selectedValue.sessionYear
                  });
                }}
                className="input w-52 text-sm"
              >
                <option value="">Select Session Member</option>
                {persons?.flatMap((person) => {
                  const shortName = person.shortName || 'UNKNOWN';
                  return Object.entries(person.sessionShortNameMap || {}).flatMap(([ year, entries ]) =>
                    entries.map((entry) => (
                      <option key={`${entry.memberId}-${year}`}
                              value={JSON.stringify({ memberId: entry.memberId, sessionYear: year })}>
                        {`${shortName}, ${year}`}
                      </option>
                    ))
                  );
                })}
              </select>
            </div>}
        </div>
      </div>
      }

      <div className="flex gap-x-6 gap-y-3 flex-wrap">
        {fieldData[formData.operation].filter((field) => field?.display !== false).map((field) =>
          <div key={field.fieldName}>
            <label className="label label--top">{field.label}:</label>
            {field.type === 'input' ? (
              <input
                type="text"
                value={formData[field.fieldName] || ''}
                onChange={(e) => setFormData({ ...formData, [field.fieldName]: e.target.value })}
                className="input block w-52 text-sm"
                required={field?.required ?? false}
                disabled={field?.disabled ?? false}
                className={`input block w-52 text-sm ${field?.disabled  ? 'text-gray-500' : 'text-black'}`}
              />
            ) : (
              <Select
                value={formData[field.fieldName] || ''}
                options={field.options}
                onChange={(e) => setFormData({ ...formData, [field.fieldName]: e.target.value })}
                name={field.fieldName}
                disabled={field?.disabled ?? false}
                className="w-52 text-sm"
              />
            )}
          </div>)}
      </div>

      <div className="flex justify-end mt-3">
        <button className="btn btn--primary w-36" type="submit">Submit</button>
      </div>
    </form>

    <Modal
      isOpen={submitSuccess !== null}
      onDismiss={closePopup}
      ariaLabel={"Confirmation of Member Update"}
    >
      <div>
        <strong className="font-semibold">{submitSuccess ? <p>{'Success!'}</p> :
          <ErrorMessage>{"Error!"}</ErrorMessage>}</strong>
        {submitSuccess ? <p>{formData.responseMessage}</p> : <ErrorMessage>{formData.responseMessage}</ErrorMessage>}
        <div className="mt-3 flex justify-end w-full">
          <button
            onClick={() => closePopup()}
            className="btn btn--secondary"
          >
            Close
          </button>
        </div>
      </div>
    </Modal>

  </div>;
};
export default MemberUI;
