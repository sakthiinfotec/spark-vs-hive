package model;

import org.apache.hadoop.hbase.util.Bytes;

public final class Constants {

	public static final byte[] CF = Bytes.toBytes("cf");
	public static final String MESSAGE_DATE = "messageDate";
	public static final String TYPE = "type";
	public static final String PATIENT_ID = "patientId";
	public static final String PATIENT_NAME = "patientName";
	public static final String DOB = "dob";
	public static final String SEX = "sex";
	public static final String ADDRESS = "address";
	public static final String MARRIED = "married";
	public static final String SSNO = "ssno";

}
