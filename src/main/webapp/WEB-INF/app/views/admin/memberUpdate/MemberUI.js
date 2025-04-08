import React, {
  useEffect,
  useState
} from "react";
import Select from "app/shared/Select";
import { handleUpdateMember } from "app/views/admin/memberUpdate/memberUpdateUtils";
import ErrorMessage from "app/shared/ErrorMessage";
import Modal from "app/shared/Modal";

const MemberUI = ({ initialData, memberType, fieldData }) => {
  const [ formData, setFormData ] = useState(initialData);

  const [ isPopupVisible, setIsPopupVisible ] = useState(false);
  const [ popupType, setPopupType ] = useState('');

  const modalTitle = popupType === 'success' ? 'Success!' : 'Error!';

  useEffect(() => {
    setFormData({...initialData})
  }, [initialData]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const response = handleUpdateMember(memberType, formData.operation, formData, fieldData);
    response.then((data) => {
      data.success ? setPopupType('success') : setPopupType('error');
      setFormData((prevData) => ({ ...prevData, responseMessage: data.message }));
      setIsPopupVisible(true);
    })

  };

  const closePopup = () => {
    setFormData(initialData);
    setIsPopupVisible(false);
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
          <option value="create">Create {memberType}</option>
          <option value="update">Update {memberType}</option>
          <option value="delete">Delete {memberType}</option>
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
      isOpen={isPopupVisible}
      onDismiss={closePopup}
      ariaLabel={"Confirmation of Member Update"}
    >
      <div>
        <strong className="font-semibold">{modalTitle === 'Error!' ? <ErrorMessage>{modalTitle}</ErrorMessage> :
          <p>{modalTitle}</p>}</strong>
        {modalTitle === 'Error!' ? <ErrorMessage>{formData.responseMessage}</ErrorMessage> :
          <p>{formData.responseMessage}</p>}
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
