package com.paulwithers.util;

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

	// private static final boolean TRACE = false;

	// /**
	// * @param formName
	// * String name of resource, used as the name of the File Resource
	// * or as a descriptor in logging
	// * @param source
	// * InputStream comprising content from Text File in Class
	// * @param mapper
	// * InputStream comprising field names and datatypes from Text
	// * File in Class
	// * @return Success / Failure
	// */
	// @SuppressWarnings("finally")
	// public static boolean loadDocsFromFile(final String formName, final InputStream source, final InputStream mapper) {
	//
	// final String method = "loadDocsFromFile()";
	//
	// if (TRACE) {
	// logEvent("Loading resouce: " + formName);
	// }
	// try {
	// final CSVReader fieldReader = new CSVReader(new InputStreamReader(mapper));
	// final List<String[]> fieldNames = fieldReader.readAll(); // All fields
	// final CSVReader reader = new CSVReader(new InputStreamReader(source));
	// final List<String[]> myEntries = reader.readAll(); // All data
	// if (TRACE) {
	// logEvent("Loaded resource, creating documents...");
	// }
	//
	// final Session session = Factory.getSession(SessionType.CURRENT);
	// final Database db = session.getCurrentDatabase();
	// int docCount = 0;
	//
	// logEvent("Loaded resource, creating documents...");
	// for (final String[] docData : myEntries) {
	// final Document doc = db.createDocument();
	// try {
	// if (fieldNames.size() < docData.length) {
	// CSV_Util.staticConsole(method, "Incorrect number of elements on line " + Integer.toString(docCount) + ": "
	// + StringUtils.join(",", docData));
	// } else {
	// doc.replaceItemValue("Form", formName);
	// createDoc(doc, fieldNames, docData, docCount);
	// }
	// } finally {
	// doc.recycle();
	// }
	// docCount++;
	// }
	// CSV_Util.staticConsole(method, "Created " + Integer.toString(docCount) + " documents");
	//
	// return true;
	// } catch (final Exception e) {
	// ServletUtils.handleException(e, true, "formName: ".concat(formName));
	// return false;
	// }
	// }
	//
	//
	// /**
	// * @param formName
	// * Form name to import to
	// * @param srcResourceName
	// * Name for a File Resource in the database from which to import
	// * @param srcMapperName
	// * Name for a File Resource in the database to give field names
	// * and datatypes
	// * @return Success or failure
	// */
	// public static boolean loadDocsFromFileResource(final String formName, final String srcResourceName, final String srcMapperName) {
	//
	// final boolean retVal = false;
	// try {
	// final InputStream source = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(srcResourceName);
	// final InputStream mapper = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(srcMapperName);
	// if (loadDocsFromFile(formName, source, mapper)) {
	// return true;
	// } else {
	// return false;
	// }
	// } catch (final Exception e) {
	// XspOpenLogUtil.logError(e);
	// return false;
	// }
	// }
	//
	//
	// private static void createDoc(final Document doc, final List<String[]> fieldNames, final String[] docData, final int docCount) {
	// try {
	// int i = 0;
	// boolean multi = false;
	// // Loop through the fields
	// for (final String[] field : fieldNames) {
	// if (field.length > 2) {
	// if ("yes".equals(field[2].toLowerCase())) {
	// multi = true;
	// }
	// }
	// if ((docData.length < i) || "".equals(docData[i])) {
	// doc.replaceItemValue(field[0], "");
	// } else {
	// // field comprises fieldName,dataType,Multi-Value
	// final String dataType = field[1].toLowerCase();
	// // Store Number Data Types
	// if ("number".equals(dataType)) {
	// try {
	// if (multi) {
	// final String[] docDataMulti = docData[i].split(",");
	// final double[] dblDataMulti = new double[docDataMulti.length];
	// for (int j = 0; j < docDataMulti.length; j++) {
	// // dblDataMulti[j] =
	// // NumberUtils.createDouble(docDataMulti[j]);
	// dblDataMulti[j] = CSV_Util.toDouble(docDataMulti[j]);
	// }
	// final Vector v = new Vector(Arrays.asList(dblDataMulti));
	// doc.replaceItemValue(field[0], v);
	// } else {
	// // doc.replaceItemValue(field[0],
	// // NumberUtils.createDouble(docData[i]));
	// doc.replaceItemValue(field[0], CSV_Util.toDouble(docData[i]));
	// }
	// } catch (final Exception e) {
	// XspOpenLogUtil.logErrorEx(e,
	// "Error setting number for " + field[0] + " on document: " + StringUtils.join(",", docData),
	// Level.SEVERE, null);
	// }
	// } else if ("date".equals(dataType) || "datetime".equals(dataType) || "time".equals(dataType)) {
	// if (multi) {
	// final String[] docDataMulti = docData[i].split(",");
	// final DateTime[] dateDataMulti = new DateTime[docDataMulti.length];
	// for (int j = 0; j < docDataMulti.length; j++) {
	// dateDataMulti[j] = ExtLibUtil.getCurrentSession().createDateTime(docDataMulti[j]);
	// }
	// final Vector v = new Vector(Arrays.asList(dateDataMulti));
	// doc.replaceItemValue(field[0], v);
	// for (int j = 0; j < docDataMulti.length; j++) {
	// dateDataMulti[j].recycle();
	// }
	// } else {
	// try {
	// final DateTime dateDocData = ExtLibUtil.getCurrentSession().createDateTime(docData[i]);
	// doc.replaceItemValue(field[0], dateDocData);
	// dateDocData.recycle();
	// } catch (final NotesException e) {
	// XspOpenLogUtil.logErrorEx(e,
	// "Error setting date for " + field[0] + " on document: " + StringUtils.join(",", docData),
	// Level.SEVERE, null);
	// }
	// }
	// } else {
	// if (multi) {
	// final String[] docDataMulti = docData[i].split(",");
	// final Vector v = new Vector(Arrays.asList(docDataMulti));
	// doc.replaceItemValue(field[0], v);
	// } else {
	// doc.replaceItemValue(field[0], docData[i]);
	// }
	// if ("author".equals(dataType)) {
	// doc.getFirstItem(field[0]).setAuthors(true);
	// } else if ("reader".equals(dataType)) {
	// doc.getFirstItem(field[0]).setReaders(true);
	// } else if ("readerauthor".equals(dataType)) {
	// doc.getFirstItem(field[0]).setReaders(true);
	// doc.getFirstItem(field[0]).setAuthors(true);
	// } else if ("names".equals(dataType)) {
	// doc.getFirstItem(field[0]).setNames(true);
	// }
	// }
	// }
	// i++; // increment
	// multi = false; // reset to false
	// }
	// // doc.computeWithForm(true, false);
	// if (TRACE) {
	// logEvent("Saving entry: " + Integer.toString(docCount));
	// }
	// doc.save();
	// } catch (final NotesException e) {
	// XspOpenLogUtil.logErrorEx(e, "Error saving document: " + StringUtils.join(",", docData), Level.SEVERE, null);
	// } catch (final Exception e) {
	// XspOpenLogUtil.logErrorEx(e, "Error saving document: " + StringUtils.join(",", docData), Level.SEVERE, null);
	// }
	// }
	//
	//
	//
	//
	//
	// public static double toDouble(final Object value) {
	// if (null == value) {
	// return 0d;
	// } else if (value instanceof Integer) {
	// return ((Integer) value).doubleValue();
	// } else if (value instanceof Double) {
	// return ((Double) value).doubleValue();
	// } else if (value instanceof String) {
	// return Double.parseDouble((String) value);
	// } else {
	// throw new DataNotCompatibleException("Cannot convert a " + value.getClass().getName() + " value of " + value + " to a double.");
	// }
	// }

}
