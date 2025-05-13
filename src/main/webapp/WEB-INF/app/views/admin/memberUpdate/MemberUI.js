import React, {
  useState, useEffect
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
  const [filterByLastName,setFilterByLastName] = useState(undefined)
  const [filterByPerson, setFilterByPerson] = useState(undefined)
  const [persons, setPersons] = useState([])
  const [filterByChamber, setFilterByChamber] = useState()

  useEffect(() => {
    setFormData({...initialData})
  }, [initialData]);

  useEffect(async() => {
    const data = await fetchPersons(filterByLastName)
      setPersons(data)
  }, [filterByLastName]);

  useEffect(() => {
    if (filterByPerson !== undefined) {
      const newData = { ...formData };

      Object.keys(formData).forEach((key) => {
        if (key === 'lbdcShortName' && 'shortName' in filterByPerson) {
          console.log("truee")
          newData[key] = filterByPerson.shortName;
        } else if (key in filterByPerson) {
          newData[key] = filterByPerson[key];
        } else if (filterByPerson.person && key in filterByPerson.person) {
          newData[key] = filterByPerson.person[key];
        }
      });
      setFormData(newData);
    }
  }, [filterByPerson]);


  useEffect(() => {
      const data = persons.filter((person) => person.chamber === filterByChamber.toUpperCase())
    setPersons(data)
  }, [filterByChamber]);

  const showFilterByLastName = !(memberType === 'Person' && formData.operation === 'create')
  const showFilterByPerson = filterByLastName && showFilterByLastName
  const availableChamberOptions = [
    ...chamberOptions.filter((option) =>
      persons.some((person) => person.chamber === option.value.toUpperCase())
    )
  ];
  const handleSubmit = async (e) => {
    e.preventDefault();
    const confirmed = window.confirm("Are you sure you want to submit?");
    if (!confirmed) return;
    const response = handleUpdateMember(memberType.replaceAll(" ", "_"), formData.operation, formData, fieldData);
    response.then((data) => {
      setSubmitSuccess(data.success);
      setFormData((prevData) => ({ ...prevData, responseMessage: data.message }));
    })
  };

  const closePopup = () => {
    setFormData(initialData);
    setSubmitSuccess(null)
  };

  return (<div className="p-3">
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
      <div className="flex gap-x-6 gap-y-3">
        {showFilterByLastName && (<div className="flex flex-col">
          <label className="label label--top">Last Name:</label>
          <input
            type="text"
            value={filterByLastName !== undefined ? filterByLastName : ''}
            onChange={(e) => setFilterByLastName(e.target.value)}
            className="input block w-52 text-sm"
          />
        </div>)}
        { showFilterByPerson && (<div className="flex flex-col">
          <label className="label label">Person:</label>
          <select
            value={filterByPerson?.memberId || ''}
            onChange={(e) => {
              const selectedId = Number(e.target.value);
              const selectedPerson = persons?.find(p => p.memberId === selectedId);
              setFilterByPerson(selectedPerson);
            }}
            className="input w-52 text-sm"
          >
            {persons?.map(person => (
              <option key={person.memberId} value={person.memberId}>
                {person.person?.fullName || 'Unknown'}
              </option>
            ))}
          </select>
        </div>)}
        {(filterByPerson &&  (memberType !== 'Person' && formData.operation !== 'create') ) && (<div>
          <label className="label label--top">Chamber:</label>
          <Select
            value={filterByChamber}
            options = {availableChamberOptions}
            onChange={(e) => {
              const newValue = e.target.value;
              if (newValue === filterByChamber) {
                setFilterByChamber("");
              } else {
                setFilterByChamber(newValue);
              }
            }}
            className="input w-52 text-sm"
          />
        </div>)}
        {(filterByChamber && (memberType === 'Session Member' && formData.operation !== 'create')) && (
          <div className="flex flex-col">
            <label className="label label--top">Session Member:</label>
            <select
              value={filterByPerson?.memberId || ''}
              onChange={(e) => {
                const selectedId = Number(e.target.value);
                const selectedPerson = persons?.find(p => p.memberId === selectedId);
                setFilterByPerson(selectedPerson);
              }}
              className="input w-52 text-sm"
            >
              {persons?.map((person) => {
                const sessionYear =
                  person.sessionYear || Object.keys(person.sessionShortNameMap || {})[0];
                const shortName = person.shortName || 'UNKNOWN';
                const label = `${shortName}, ${sessionYear}`;

                return (
                  <option key={`${person.memberId}-${sessionYear}`} value={person.memberId}>
                    {label}
                  </option>
                );
              })}
            </select>
          </div>
        )}
      </div>




      <div className="flex gap-x-6 gap-y-3 flex-wrap">
        {fieldData[formData.operation].filter((field) => field?.display !== false).map((field) => (
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
              />
            ) : (
              <Select
                value={formData[field.fieldName] || ''}
                options={field.options}
                onChange={(e) => setFormData({ ...formData, [field.fieldName]: e.target.value })}
                name={field.fieldName}
                className="w-52 text-sm"
              />
            )}
          </div>
        ))}
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
        <strong className="font-semibold">{submitSuccess ? <p>{'Success!'}</p> : <ErrorMessage>{"Error!"}</ErrorMessage>}</strong>
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

  </div>);
};
export default MemberUI;
