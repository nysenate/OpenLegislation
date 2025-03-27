import React, { useState } from 'react';
import Modal from "app/shared/Modal";
import Select from "app/shared/Select";
import ErrorMessage from "app/shared/ErrorMessage";

export default function UpdateMember() {
  const initialData = {
    id: undefined,
    personId: undefined,
    operation: 'create',
    incumbent: true,
    chamber: "Senate",
    responseMessage: undefined,
  };
  const [formData, setFormData] = useState(initialData);

  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [popupType, setPopupType] = useState('');

  const modalTitle = popupType === 'success' ? 'Success!' : 'Error!';

  const fieldData = {
    create: [
      { label: 'Person ID', type: 'input', value: formData.personId, setter: (val) => setFormData({ ...formData, personId: val }), fieldName: 'personId', required: true },
      { label: 'Chamber', type: 'select', value: formData.chamber, options: [{ value: "Senate", label: "Senate" }, { value: "Assembly", label: "Assembly" }], setter: (val) => setFormData({ ...formData, chamber: val }), fieldName: 'chamber' },
    ],
    update: [
      { label: 'Member ID', type: 'input', value: formData.id, setter: (val) => setFormData({ ...formData, id: val }), fieldName: 'id', required: true },
      { label: 'Incumbent', type: 'select', value: formData.incumbent, options: [{ value: "true", label: "True" }, { value: "false", label: "False" }], setter: (val) => setFormData({ ...formData, incumbent: val }), fieldName: 'incumbent' },
    ],
    delete: [
      { label: 'Member ID', type: 'input', value: formData.id, setter: (val) => setFormData({ ...formData, id: val }), fieldName: 'id', required: true },
    ]
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let apiEndpoint = `/api/3/members/MEMBER?action=${formData.operation.toUpperCase()}`;
    let payload = {};

    const createFields = ['personId', 'chamber'];
    const updateFields = ['incumbent'];

    switch (formData.operation) {
      case 'create':
        payload = {};
        createFields.forEach(field => {
          if (formData[field] !== undefined) {
            payload[field] = formData[field]; // Only add if value is not undefined
          }
        });
        break;

      case 'update':
        payload = { id: formData.id };
        updateFields.forEach(field => {
          if (formData[field] !== undefined) {
            payload[field] = formData[field]; // Only add if value is not undefined
          }
        });
        break;

      case 'delete':
        payload = { id: formData.id };
        break;

      default:
        return;
    }

    try {
      const response = await fetch(apiEndpoint, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
        credentials: 'include',
      });

      const result = await response.json();
      if (response.ok) {
        setFormData((prevData) => ({ ...prevData, responseMessage: result.message }));
        setPopupType('success');
      } else {
        setFormData((prevData) => ({ ...prevData, responseMessage: result.message }));
        setPopupType('error');
      }
    } catch (error) {
      setFormData((prevData) => ({ ...prevData, responseMessage: 'Error: ' + error.message }));
      setPopupType('error');
    } finally {
      setIsPopupVisible(true);
    }
  };

  const closePopup = () => {
    setFormData(initialData);
    setIsPopupVisible(false);
  };

  return (
    <div className="p-3">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="flex justify-end">
          <label className="label label--top">Choose Operation:</label>
          <select
            id="operation"
            value={formData.operation}
            onChange={(e) => setFormData({ ...formData, operation: e.target.value })}
            className="select block ml-2"
          >
            <option value="create">Create Member</option>
            <option value="update">Update Member</option>
            <option value="delete">Delete Member</option>
          </select>
        </div>

        <div className="flex gap-x-6 gap-y-3 flex-wrap">
          {fieldData[formData.operation].map((field) => (
            <div key={field.fieldName}>
              <label className="label label--top">{field.label}:</label>
              {field.type === 'input' ? (
                <input
                  type="text"
                  value={field.value !== undefined ? field.value : ''} // Ensure undefined is handled
                  onChange={(e) => field.setter(e.target.value)}
                  className="input block w-52 text-sm"
                  required={field?.required ?? false}
                />
              ) : (
                <Select
                  value={field.value !== undefined ? field.value : ''} // Ensure undefined is handled
                  options={field.options}
                  onChange={(e) => field.setter(e.target.value)}
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
        isOpen={isPopupVisible}
        onDismiss={closePopup}
        ariaLabel={"Confirmation of Member Update"}
      >
        <div>
          <strong className="font-semibold">{modalTitle === 'Error!' ? <ErrorMessage>{modalTitle}</ErrorMessage> : <p>{modalTitle}</p>}</strong>
          {modalTitle === 'Error!' ? <ErrorMessage>{formData.responseMessage}</ErrorMessage> : <p>{formData.responseMessage}</p>}
          <div className="mt-3 flex justify-end w-full">
            <button
              onClick={closePopup}
              className="btn btn--secondary"
            >
              Close
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
