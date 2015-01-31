package lib;

import model.ADTMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.hbase.util.Bytes;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.XAD;
import ca.uhn.hl7v2.model.v251.datatype.XPN;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.message.ADT_A03;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.parser.Parser;

public class MessageUtil {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

	public static long getReverseTimestamp(String datetime) throws ParseException {
		Date date = sdf.parse(datetime);
		return (Long.MAX_VALUE - date.getTime());
	}

	public static String formatDateTime(String datetime) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
		Date date = sdf.parse(datetime);
		return df.format(date);
	}

	public static String formatDOB(String datetime) throws ParseException {
		SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
		Date date = df.parse(datetime);
		return dmy.format(date);
	}

	public static byte[] makeAnalyticsRowKey(final String patientIdStr, final String messageDateStr) throws ParseException {
		return Bytes.toBytes(patientIdStr + "_" + getReverseTimestamp(messageDateStr));
	}

	public static ADTMessage parseHL7Message(String message) throws Exception {

		HapiContext context = new DefaultHapiContext();
		Parser p = context.getGenericParser();
		Message hapiMsg = p.parse(message);
		;

		ADTMessage adtMsg = new ADTMessage();
		adtMsg.setType(hapiMsg.getName());

		MSH msh = null;
		if (hapiMsg instanceof ADT_A03) {
			ADT_A03 disMsg = (ADT_A03) hapiMsg;
			msh = disMsg.getMSH();
			parseADTA03Message(adtMsg, disMsg);
		} else if (hapiMsg instanceof ADT_A01) {
			ADT_A01 admMsg = (ADT_A01) hapiMsg;
			msh = admMsg.getMSH();
			parseADTA01Message(adtMsg, admMsg);
		}
		adtMsg.setMessageDate(msh.getDateTimeOfMessage().getTime().getValue());
		return adtMsg;
	}

	public static void parseADTA01Message(ADTMessage adtMsg, ADT_A01 admMsg) {
		adtMsg.setSsno(admMsg.getPID().getSSNNumberPatient().getValue());
		adtMsg.setPatientId(admMsg.getPID().getPatientID().getIDNumber().getValue());
		adtMsg.setDob(admMsg.getPID().getDateTimeOfBirth().getTime().getValue());
		adtMsg.setMarried(admMsg.getPID().getMaritalStatus().getIdentifier().getValue().equals("S") ? "Y" : "N");
		adtMsg.setSex(admMsg.getPID().getAdministrativeSex().getValue());

		for (XAD ad : admMsg.getPID().getPatientAddress()) {
			String dno = ad.getStreetAddress().getStreetOrMailingAddress().getValue();
			String city = ad.getCity().getValue();
			String state = ad.getStateOrProvince().getValue();
			String zipcode = ad.getZipOrPostalCode().getValue();
			adtMsg.setAddress(dno + ", " + city + ", " + state + ", " + zipcode);
		}

		// PN patientName = adtMsg.getPID().getPatientName();
		XPN[] pnames = admMsg.getPID().getPatientName();
		for (XPN pn : pnames) {
			String familyName = pn.getFamilyName().getSurname().getValue();
			String givenName = pn.getGivenName().getValue();
			adtMsg.setPatientName(givenName + " " + familyName);
		}
	}

	public static void parseADTA03Message(ADTMessage adtMsg, ADT_A03 dischMsg) {
		adtMsg.setSsno(dischMsg.getPID().getSSNNumberPatient().getValue());
		adtMsg.setPatientId(dischMsg.getPID().getPatientID().getIDNumber().getValue());
		adtMsg.setDob(dischMsg.getPID().getDateTimeOfBirth().getTime().getValue());
		adtMsg.setMarried(dischMsg.getPID().getMaritalStatus().getIdentifier().getValue().equals("S") ? "Y" : "N");
		adtMsg.setSex(dischMsg.getPID().getAdministrativeSex().getValue());

		for (XAD ad : dischMsg.getPID().getPatientAddress()) {
			String dno = ad.getStreetAddress().getStreetOrMailingAddress().getValue();
			String city = ad.getCity().getValue();
			String state = ad.getStateOrProvince().getValue();
			String zipcode = ad.getZipOrPostalCode().getValue();
			adtMsg.setAddress(dno + ", " + city + ", " + state + ", " + zipcode);
		}

		// PN patientName = adtMsg.getPID().getPatientName();
		XPN[] pnames = dischMsg.getPID().getPatientName();
		for (XPN pn : pnames) {
			String familyName = pn.getFamilyName().getSurname().getValue();
			String givenName = pn.getGivenName().getValue();
			adtMsg.setPatientName(givenName + " " + familyName);
		}
	}

}
