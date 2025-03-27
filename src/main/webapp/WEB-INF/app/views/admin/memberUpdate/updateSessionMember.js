import React, { useState } from 'react';
import Select from "app/shared/Select";
import Modal from "app/shared/Modal";
import ErrorMessage from "app/shared/ErrorMessage";
import { sessionOptions } from "app/views/bills/search/billSearchUtils";

export default function UpdateSessionMember() {
  const initialFormData = {
    id: undefined,
    memberId: undefined,
    sessionYear: undefined,
    districtCode: undefined,
    lbdcShortName: undefined,
    alternate: true,
    operation: 'create',
    responseMessage: undefined,
  };
  const [formData, setFormData] = useState(initialFormData);

  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [popupType, setPopupType] = useState('');

  const modalTitle = popupType === 'success' ? 'Success!' : 'Error!';

  const fieldData = {
    create: [
      { label: 'Member Id', type: 'input', value: formData.memberId, setter: (val) => setFormData({ ...formData, memberId: val }), fieldName: 'memberId', required: true },
      { label: 'Session Year', type: 'select', options: sessionOptions, value: formData.sessionYear, setter: (val) => setFormData({ ...formData, sessionYear: val }), fieldName: 'sessionYear', required: true },
      { label: 'Lbdc Short Name', type: 'input', value: formData.lbdcShortName, setter: (val) => setFormData({ ...formData, lbdcShortName: val }), fieldName: 'lbdcShortName', required: true },
      { label: 'District Code', type: 'input', value: formData.districtCode, setter: (val) => setFormData({ ...formData, districtCode: val }), fieldName: 'districtCode', required: true },
    ],
    update: [
      { label: 'Session Member Id', type: 'input', value: formData.id, setter: (val) => setFormData({ ...formData, id: val }), fieldName: 'id', required: true },
      { label: 'Alternate', type: 'select', options: [{ value: "true", label: "True" }, { value: "false", label: "False" }], value: formData.alternate, setter: (val) => setFormData({ ...formData, alternate: val }), fieldName: 'alternate' },
      { label: 'District Code', type: 'input', value: formData.districtCode, setter: (val) => setFormData({ ...formData, districtCode: val }), fieldName: 'districtCode' },
    ],
    delete: [
      { label: 'Session Member Id', type: 'input', value: formData.id, setter: (val) => setFormData({ ...formData, id: val }), fieldName: 'id', required: true },
    ]
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let apiEndpoint = `/api/3/members/SESSION?action=${formData.operation.toUpperCase()}`;
    let payload = {};

    const createFields = ['memberId', 'sessionYear', 'lbdcShortName', 'districtCode'];
    const updateFields = ['alternate', 'districtCode'];

    switch (formData.operation) {
      case 'create':
        payload = {};
        createFields.forEach(field => {
          if (formData[field]) {
            payload[field] = formData[field];
          }
        });
        break;

      case 'update':
        payload = { id: formData.id };
        updateFields.forEach(field => {
          if (formData[field]) {
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
    setFormData(initialFormData);
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
            <option value="create">Create Session Member</option>
            <option value="update">Update Session Member</option>
            <option value="delete">Delete Session Member</option>
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
                  value={field.value || ''}
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
