package com.paulwithers.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.NotesException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.openntf.domino.xsp.XspOpenLogUtil;

import au.com.bytecode.opencsv.CSVReader;

import com.ibm.xsp.extlib.util.ExtLibUtil;

/**
 * @author withersp
 * @since 05/01/2013
 * 
 *        To import from a comma-delimited text file stored in a class, use:
 * 
 *        InputStream people =
 *        DataInitializer.class.getResourceAsStream("People"); InputStream
 *        peopleMapper =
 *        DataInitializer.class.getResourceAsStream("PeopleMapper"); return
 *        CSV_Util.loadDocsFromFile("People", people, peopleMapper);
 * 
 *        To import from a comma-delimited File Resource in the NSF, use:
 * 
 *        return CSV_Util.loadDocsFromFileResource("People", "People",
 *        "PeopleMapper");
 */
public class CSV_Util {

	private static final boolean TRACE = false;

	/**
	 * @param formName
	 *            String name of resource, used as the name of the File Resource
	 *            or as a descriptor in logging
	 * @param source
	 *            InputStream comprising content from Text File in Class
	 * @param mapper
	 *            InputStream comprising field names and datatypes from Text
	 *            File in Class
	 * @return Success / Failure
	 */
	@SuppressWarnings("finally")
	public static boolean loadDocsFromFile(String formName, InputStream source,
			InputStream mapper) {

		if (TRACE) {
			logEvent("Loading resouce: " + formName);
		}
		try {
			CSVReader fieldReader = new CSVReader(new InputStreamReader(mapper));
			List<String[]> fieldNames = fieldReader.readAll(); // All fields
			CSVReader reader = new CSVReader(new InputStreamReader(source));
			List<String[]> myEntries = reader.readAll(); // All data
			if (TRACE) {
				logEvent("Loaded resource, creating documents...");
			}

			Database db = ExtLibUtil.getCurrentDatabase();
			int docCount = 0;

			logEvent("Loaded resource, creating documents...");
			for (String[] docData : myEntries) {
				Document doc = db.createDocument();
				try {
					if (fieldNames.size() < docData.length) {
						XspOpenLogUtil.logErrorEx(new Throwable(),
								"Incorrect number of elements on line "
										+ Integer.toString(docCount) + ": "
										+ StringUtils.join(docData, ","),
								Level.SEVERE, null);

					} else {
						doc.replaceItemValue("Form", formName);
						createDoc(doc, fieldNames, docData, docCount);
					}
				} finally {
					doc.recycle();
				}
				docCount++;
			}
			XspOpenLogUtil.logEvent(new Throwable(), "Created "
					+ Integer.toString(docCount) + " documents", Level.FINE,
					null);

			return true;
		} catch (Exception e) {
			XspOpenLogUtil.logError(e);
			return false;
		}
	}

	/**
	 * @param formName
	 *            Form name to import to
	 * @param srcResourceName
	 *            Name for a File Resource in the database from which to import
	 * @param srcMapperName
	 *            Name for a File Resource in the database to give field names
	 *            and datatypes
	 * @return Success or failure
	 */
	public static boolean loadDocsFromFileResource(String formName,
			String srcResourceName, String srcMapperName) {

		boolean retVal = false;
		try {
			InputStream source = FacesContext.getCurrentInstance()
					.getExternalContext().getResourceAsStream(srcResourceName);
			InputStream mapper = FacesContext.getCurrentInstance()
					.getExternalContext().getResourceAsStream(srcMapperName);
			if (loadDocsFromFile(formName, source, mapper)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			XspOpenLogUtil.logError(e);
			return false;
		}
	}

	private static void createDoc(Document doc, List<String[]> fieldNames,
			String[] docData, int docCount) {
		try {
			int i = 0;
			boolean multi = false;
			// Loop through the fields
			for (String[] field : fieldNames) {
				if (field.length > 2) {
					if ("yes".equals(field[2].toLowerCase())) {
						multi = true;
					}
				}
				if (docData.length < i || "".equals(docData[i])) {
					doc.replaceItemValue(field[0], "");
				} else {
					// field comprises fieldName,dataType,Multi-Value
					String dataType = field[1].toLowerCase();
					// Store Number Data Types
					if ("number".equals(dataType)) {
						try {
							if (multi) {
								String[] docDataMulti = docData[i].split(",");
								double[] dblDataMulti = new double[docDataMulti.length];
								for (int j = 0; j < docDataMulti.length; j++) {
									dblDataMulti[j] = NumberUtils
											.createDouble(docDataMulti[j]);
								}
								Vector v = new Vector(Arrays
										.asList(dblDataMulti));
								doc.replaceItemValue(field[0], v);
							} else {
								doc.replaceItemValue(field[0], NumberUtils
										.createDouble(docData[i]));
							}
						} catch (Exception e) {
							XspOpenLogUtil.logErrorEx(e,
									"Error setting number for " + field[0]
											+ " on document: "
											+ StringUtils.join(docData, ","),
									Level.SEVERE, null);
						}
					} else if ("date".equals(dataType)
							|| "datetime".equals(dataType)
							|| "time".equals(dataType)) {
						if (multi) {
							String[] docDataMulti = docData[i].split(",");
							DateTime[] dateDataMulti = new DateTime[docDataMulti.length];
							for (int j = 0; j < docDataMulti.length; j++) {
								dateDataMulti[j] = ExtLibUtil
										.getCurrentSession().createDateTime(
												docDataMulti[j]);
							}
							Vector v = new Vector(Arrays.asList(dateDataMulti));
							doc.replaceItemValue(field[0], v);
							for (int j = 0; j < docDataMulti.length; j++) {
								dateDataMulti[j].recycle();
							}
						} else {
							try {
								DateTime dateDocData = ExtLibUtil
										.getCurrentSession().createDateTime(
												docData[i]);
								doc.replaceItemValue(field[0], dateDocData);
								dateDocData.recycle();
							} catch (NotesException e) {
								XspOpenLogUtil.logErrorEx(e,
										"Error setting date for "
												+ field[0]
												+ " on document: "
												+ StringUtils
														.join(docData, ","),
										Level.SEVERE, null);
							}
						}
					} else {
						if (multi) {
							String[] docDataMulti = docData[i].split(",");
							Vector v = new Vector(Arrays.asList(docDataMulti));
							doc.replaceItemValue(field[0], v);
						} else {
							doc.replaceItemValue(field[0], docData[i]);
						}
						if ("author".equals(dataType)) {
							doc.getFirstItem(field[0]).setAuthors(true);
						} else if ("reader".equals(dataType)) {
							doc.getFirstItem(field[0]).setReaders(true);
						} else if ("readerauthor".equals(dataType)) {
							doc.getFirstItem(field[0]).setReaders(true);
							doc.getFirstItem(field[0]).setAuthors(true);
						} else if ("names".equals(dataType)) {
							doc.getFirstItem(field[0]).setNames(true);
						}
					}
				}
				i++; // increment
				multi = false; // reset to false
			}
			// doc.computeWithForm(true, false);
			if (TRACE) {
				logEvent("Saving entry: " + Integer.toString(docCount));
			}
			doc.save();
		} catch (NotesException e) {
			XspOpenLogUtil.logErrorEx(e, "Error saving document: "
					+ StringUtils.join(docData, ","), Level.SEVERE, null);
		} catch (Exception e) {
			XspOpenLogUtil.logErrorEx(e, "Error saving document: "
					+ StringUtils.join(docData, ","), Level.SEVERE, null);
		}
	}

	private static void logEvent(String msg) {
		XspOpenLogUtil.logEvent(new Throwable(), msg, Level.FINE, null);
	}

}