import React, {
  useState, useEffect
} from "react";
import Select from "app/shared/Select";
import { handleUpdateMember } from "app/views/admin/memberUpdate/MemberUpdateUtils";
import ErrorMessage from "app/shared/ErrorMessage";
import Modal from "app/shared/Modal";

const MemberUI = ({ initialData, memberType, fieldData }) => {
  const [ formData, setFormData ] = useState(initialData);
  const [ submitSuccess, setSubmitSuccess ] = useState(null);

  useEffect(() => {
    setFormData({...initialData})
  }, [initialData]);

  const handleSubmit = async (e) => {
    e.preventDefault();
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

      <div className="flex gap-x-6 gap-y-3 flex-wrap">
        {fieldData[formData.operation].map((field) => (
          <div key={field.fieldName}>
            <label className="label label--top">{field.label}:</label>
            {field.type === 'input' ? (
              <input
                type="text"
                value={formData[field.fieldName] !== undefined ? formData[field.fieldName] : ''}
                onChange={(e) => setFormData({ ...formData, [field.fieldName]: e.target.value })}
                className="input block w-52 text-sm"
                required={field?.required ?? false}
              />
            ) : (
              <Select
                value={formData[field.fieldName] !== undefined ? formData[field.fieldName] : ''}
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
