package controllers;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import play.*;
import play.mvc.*;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import scala.Tuple2;

import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.HTable;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import views.html.*;
import model.ADTMessage;
import lib.MessageUtil;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.XAD;
import ca.uhn.hl7v2.model.v251.datatype.XPN;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.message.ADT_A03;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.XPN;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.Parser;

public class Application extends Controller {

	private static Configuration conf = HBaseConfiguration.create();
	private static final byte[] CF = Bytes.toBytes("cf");
	private static final byte[] messageDate = Bytes.toBytes("messageDate");
	private static final byte[] type = Bytes.toBytes("type");
	private static final byte[] patientId = Bytes.toBytes("patientId");
	private static final byte[] patientName = Bytes.toBytes("patientName");
	private static final byte[] dob = Bytes.toBytes("dob");
	private static final byte[] sex = Bytes.toBytes("sex");
	private static final byte[] address = Bytes.toBytes("address");
	private static final byte[] married = Bytes.toBytes("married");
	private static final byte[] ssno = Bytes.toBytes("ssno");

	private static final String APP_NAME = "SparkHBaseClient";
	private static final String MASTER = "spark://ip-10-148-99-179:7077";
	private static final String TABLE_NAME = "analytics";
	private static final String XMLFILE = "/opt/hbase-0.98.8-hadoop1/conf/hbase-site.xml";

	/**
	 * Writes the given scan into a Base64 encoded string.
	 * 
	 * @param scan
	 *            The scan to write out.
	 * @return The scan saved in a Base64 encoded string.
	 * @throws IOException
	 *             When writing the scan fails.
	 */
	private static String convertScanToString(Scan scan) throws IOException {
		ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
		return Base64.encodeBytes(proto.toByteArray());
	}

	private static JavaPairRDD<ImmutableBytesWritable, org.apache.hadoop.hbase.client.Result> getHTableDataAsRDD(String patientIdStr, String typeStr) throws IOException {
		SparkConf sparkConf = new SparkConf().setAppName(APP_NAME).setMaster(MASTER);
		JavaSparkContext sc = new JavaSparkContext(sparkConf);
		Configuration conf = HBaseConfiguration.create();

		if(patientIdStr == null) {
			patientIdStr = "1000";
		}

		final byte[] startRow = Bytes.toBytes(patientIdStr + "_");
		final byte[] stopRow = Bytes.toBytes(patientIdStr + "_~");
		Scan scan = new Scan(startRow, stopRow);

		if(typeStr != null) {
			SingleColumnValueFilter typeFilter = new SingleColumnValueFilter(CF, type, CompareOp.EQUAL,	typeStr.getBytes());
			typeFilter.setFilterIfMissing(true);
			scan.setFilter(typeFilter);
		}
		
		conf.set(TableInputFormat.INPUT_TABLE, TABLE_NAME);
		conf.set(TableInputFormat.SCAN, convertScanToString(scan));
		return sc.newAPIHadoopRDD(conf, TableInputFormat.class, ImmutableBytesWritable.class, org.apache.hadoop.hbase.client.Result.class);
	}
	
	public static long getTotalRecordsCount() {
		JavaPairRDD<ImmutableBytesWritable, org.apache.hadoop.hbase.client.Result> hBaseRDD = null;
		try {
			hBaseRDD = getHTableDataAsRDD(null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hBaseRDD== null ? 0 : hBaseRDD.count();
	}

	private static void persistPatientDetails(ADTMessage msg) throws Exception {
		conf.addResource(XMLFILE);
		HTable table = new HTable(conf, "analytics");
		String patientIdStr = msg.getPatientId();
		String messageDateStr = msg.getMessageDate();
		Put put = new Put(MessageUtil.makeAnalyticsRowKey(patientIdStr, messageDateStr));
		put.add(CF, messageDate, Bytes.toBytes(MessageUtil.formatDateTime(messageDateStr)));
		put.add(CF, type, Bytes.toBytes(msg.getType()));
		put.add(CF, patientId, Bytes.toBytes(patientIdStr));
		put.add(CF, patientName, Bytes.toBytes(msg.getPatientName()));
		put.add(CF, dob, Bytes.toBytes(MessageUtil.formatDOB(msg.getDob())));
		put.add(CF, sex, Bytes.toBytes(msg.getSex()));
		put.add(CF, address, Bytes.toBytes(msg.getAddress()));
		put.add(CF, married, Bytes.toBytes(msg.getMarried()));
		put.add(CF, ssno, Bytes.toBytes(msg.getSsno()));
		table.put(put);
		table.flushCommits();
		table.close();    	
	}

		@BodyParser.Of(BodyParser.Json.class)
    public static play.mvc.Result queryRealtime() {
		  JsonNode json = request().body().asJson();
		  ObjectNode response = Json.newObject();
		  String patientIdStr = json.findPath("patientId").textValue();
		  String typeStr = json.findPath("type").textValue();
			try {
				JavaPairRDD<ImmutableBytesWritable, org.apache.hadoop.hbase.client.Result> hBaseRDD = getHTableDataAsRDD(patientIdStr, typeStr);

				JavaRDD<ADTMessage> resultRDD = hBaseRDD.map(new Function<Tuple2<ImmutableBytesWritable, org.apache.hadoop.hbase.client.Result>, ADTMessage>() {
					private static final long serialVersionUID = 1L;

					private ADTMessage buildADTMessage(org.apache.hadoop.hbase.client.Result result) {
						ADTMessage msg = new ADTMessage();
						msg.setMessageDate(Bytes.toString(result.getValue(CF, messageDate)));
						msg.setType(Bytes.toString(result.getValue(CF, type)));
						msg.setPatientId(Bytes.toString(result.getValue(CF, patientId)));
						msg.setPatientName(Bytes.toString(result.getValue(CF, patientName)));
						msg.setDob(Bytes.toString(result.getValue(CF, dob)));
						msg.setSex(Bytes.toString(result.getValue(CF, sex)));
						msg.setAddress(Bytes.toString(result.getValue(CF, address)));
						msg.setMarried(Bytes.toString(result.getValue(CF, married)));
						msg.setSsno(Bytes.toString(result.getValue(CF, ssno)));
						return msg;
					}

					public ADTMessage call(Tuple2<ImmutableBytesWritable, org.apache.hadoop.hbase.client.Result> tuple) throws Exception {
						org.apache.hadoop.hbase.client.Result result = tuple._2();
						return buildADTMessage(result);
					}

				});

				List<ADTMessage> msgList = resultRDD.collect();
				response.put("data", play.libs.Json.toJson(msgList));
				// for (ADTMessage msg : msgList) {
				// 	System.out.println("patientId:" + msg.getPatientId() + ", patientName:" + msg.getPatientName());
				// }
		    return ok(response);

			} catch (Exception e) {
	    		e.printStackTrace();
	    		return internalServerError("Error querying realtime data.");
			}
    }

	@BodyParser.Of(BodyParser.Json.class)
    public static play.mvc.Result queryBatch() {
	  JsonNode json = request().body().asJson();
	  ObjectNode result = Json.newObject();
	  result.put("query", "batch");
	  String name = json.findPath("name").textValue();
	  if(name == null) {
	    result.put("message", "Missing parameter [name]");
	    return badRequest(result);
	  } else {
	    return ok(result);
	  }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static play.mvc.Result create() {
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");
		} else {
			String message = json.findPath("message").textValue();
			if(message == null) {
			  	return badRequest("Missing parameter [message]");
			} else {
		    	try{
		    		persistPatientDetails(MessageUtil.parseHL7Message(message));
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    		return internalServerError("Error in persisting data ...");
		    	}
			  	return ok("Data persisted.");
			}
		}
    }

}
