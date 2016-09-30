package com.paulwithers.util;

import java.io.InputStream;

public class TestImporter {

	public TestImporter() {

	}

	public static boolean loadContactEntries() {

		boolean retVal = false;
		InputStream people = TestImporter.class.getResourceAsStream("People");
		InputStream peopleMapper = TestImporter.class.getResourceAsStream("PeopleMapper");

		retVal = CSV_Util.loadDocsFromFile("Contact", people, peopleMapper);
		return retVal;
	}
}
