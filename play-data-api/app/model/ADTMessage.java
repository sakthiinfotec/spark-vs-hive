package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds a single record for ADTMessage
 * 
 * @author Sakthi
 */
public class ADTMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String messageDate;
	private String type;
	private String patientId;
	private String patientName;
	private String dob;
	private String sex;
	private String address;
	private String married;
	private String ssno;

	public String getMessageDate() {
		return messageDate;
	}

	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMarried() {
		return married;
	}

	public void setMarried(String married) {
		this.married = married;
	}

	public String getSsno() {
		return ssno;
	}

	public void setSsno(String ssno) {
		this.ssno = ssno;
	}

	/**
	 * The method maintains <field, value> map for a single notes record.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getFieldMap() {
		Map<String, String> fieldMap = new HashMap<String, String>();
		fieldMap.put(Constants.MESSAGE_DATE, this.messageDate);
		fieldMap.put(Constants.TYPE, this.type);
		fieldMap.put(Constants.PATIENT_ID, this.patientId);
		fieldMap.put(Constants.PATIENT_NAME, this.patientName);
		fieldMap.put(Constants.DOB, this.dob);
		fieldMap.put(Constants.SEX, this.sex);
		fieldMap.put(Constants.ADDRESS, this.address);
		fieldMap.put(Constants.MARRIED, this.married);
		fieldMap.put(Constants.SSNO, this.ssno);
		return fieldMap;
	}

}
