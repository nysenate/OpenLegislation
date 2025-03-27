import React, { useState, useEffect } from 'react';
import Select from "app/shared/Select";
import Modal from "app/shared/Modal";
import ErrorMessage from "app/shared/ErrorMessage";

export default function UpdatePerson() {

  const intialData =  {
    id: undefined,
    operation: 'create',
    firstName: undefined,
    middleName: undefined,
    lastName: undefined,
    email: undefined,
    imgName: undefined,
    suffix: undefined,
    responseMessage: '',
  };

  const [formData, setFormData] = useState(intialData);
  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [popupType, setPopupType] = useState('');

  const modalTitle = popupType === 'success' ? 'Success!' : 'Error!';

  const fieldData = {
    create: [
      { label: 'Suffix', type: 'input', value: formData.suffix, setter: (val) => setFormData({ ...formData, suffix: val }), fieldName: 'suffix' },
      { label: 'First Name', type: 'input', value: formData.firstName, setter: (val) => setFormData({ ...formData, firstName: val }), fieldName: 'firstName', required: true },
      { label: 'Middle Name', type: 'input', value: formData.middleName, setter: (val) => setFormData({ ...formData, middleName: val }), fieldName: 'middleName' },
      { label: 'Last Name', type: 'input', value: formData.lastName, setter: (val) => setFormData({ ...formData, lastName: val }), fieldName: 'lastName', required: true },
      { label: 'Email Address', type: 'input', value: formData.email, setter: (val) => setFormData({ ...formData, email: val }), fieldName: 'email' },
      { label: 'Profile Picture', type: 'input', value: formData.imgName, setter: (val) => setFormData({ ...formData, imgName: val }), fieldName: 'imgName' },
    ],
    update: [
      { label: 'Person Id', type: 'input', value: formData.id, setter: (val) => setFormData({ ...formData, id: val }), fieldName: 'id', required: true },
      { label: 'Suffix', type: 'input', value: formData.suffix, setter: (val) => setFormData({ ...formData, suffix: val }), fieldName: 'suffix' },
      { label: 'First Name', type: 'input', value: formData.firstName, setter: (val) => setFormData({ ...formData, firstName: val }), fieldName: 'firstName' },
      { label: 'Middle Name', type: 'input', value: formData.middleName, setter: (val) => setFormData({ ...formData, middleName: val }), fieldName: 'middleName' },
      { label: 'Last Name', type: 'input', value: formData.lastName, setter: (val) => setFormData({ ...formData, lastName: val }), fieldName: 'lastName' },
      { label: 'Email Address', type: 'input', value: formData.email, setter: (val) => setFormData({ ...formData, email: val }), fieldName: 'email' },
      { label: 'Profile Picture', type: 'input', value: formData.imgName, setter: (val) => setFormData({ ...formData, imgName: val }), fieldName: 'imgName' },
    ],
    delete: [
      { label: 'Person Id', type: 'input', value: formData.id, setter: (val) => setFormData({ ...formData, id: val }), fieldName: 'id', required: true },
    ]
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let apiEndpoint = `/api/3/members/PERSON?action=${formData.operation.toUpperCase()}`;
    let payload = {};

    const fields = ['firstName', 'lastName', 'suffix', 'middleName', 'imgName', 'email'];

    switch (formData.operation) {
      case 'create':
      case 'update':
        payload = { id: formData.id };
        fields.forEach(field => {
          if (formData[field] !== undefined) {
            payload[field] = formData[field];
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
    setFormData(intialData);
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
            <option value="create">Create Person</option>
            <option value="update">Update Person</option>
            <option value="delete">Delete Person</option>
          </select>
        </div>

        <div className="flex gap-x-6 gap-y-3 flex-wrap">
          {fieldData[formData.operation].map((field) => (
            <div key={field.fieldName}>
              <label className="label label--top">{field.label}:</label>
              {field.type === 'input' ? (
                <input
                  type="text"
                  value={field.value || ''}
                  onChange={(e) => field.setter(e.target.value)}
                  className="input block w-52 text-sm"
                  required={field?.required ?? false}
                />
              ) : (
                <Select
                  value={field.value || ''}  // Handle undefined for select as well
                  options={field.options}
                  onChange={(e) => field.setter(e.target.value)}
                  name={field.name}
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
