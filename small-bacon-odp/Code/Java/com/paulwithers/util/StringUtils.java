package com.paulwithers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.openntf.domino.utils.CollectionUtils;
import org.openntf.domino.utils.Strings;

public enum StringUtils {
	; // NO INSTANCE MEMBERS

	/**
	 * Returns a new String composed of copies of the String elements joined together with a copy of the specified delimiter.
	 * 
	 * For example,
	 * 
	 * String message = String.join("-", "Java", "is", "cool"); // message returned is: "Java-is-cool"
	 * 
	 * Note that if an element is null, then it will not be included in the result.
	 * 
	 * @param delimiter
	 *        the delimiter that separates each element
	 * @param elements
	 *        the elements to join together.
	 * 
	 * @return a new String that is composed of the elements separated by the delimiter
	 */
	public static String join(final String delimiter, final String... elements) {
		return null;
	}


	/**
	 * Returns a new String composed of copies of the String elements joined together with a copy of the specified delimiter.
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * List&lt;String&gt; strings = new LinkedList&lt;&gt;();
	 * strings.add(&quot;Java&quot;);strings.add(&quot;is&quot;);
	 * strings.add(&quot;cool&quot;);
	 * String message = String.join(&quot; &quot;, strings);
	 * //message returned is: &quot;Java is cool&quot;
	 * 
	 * Set&lt;String&gt; strings = new LinkedHashSet&lt;&gt;();
	 * strings.add(&quot;Java&quot;); strings.add(&quot;is&quot;);
	 * strings.add(&quot;very&quot;); strings.add(&quot;cool&quot;);
	 * String message = String.join(&quot;-&quot;, strings);
	 * //message returned is: &quot;Java-is-very-cool&quot;
	 * }
	 * </pre>
	 * 
	 * Note that if an element is null, then it will not be included in the result.
	 * 
	 * @param delimiter
	 *        the delimiter that separates each element
	 * @param elements
	 *        an Iterable that will have its elements joined together.
	 * 
	 * @return a new String that is composed from the elements argument
	 */
	public static String join(final String delimiter, final Iterable<? extends String> elements) {
		return null;
	}


	/**
	 * Returns a new String composed of String values of the Object(s) joined together with a copy of the specified delimiter.
	 * 
	 * Note that if an element is null, then it will not be included in the result.
	 * 
	 * @param delimiter
	 *        the delimiter that separates each element
	 * @param objects
	 *        the objects to join together.
	 * 
	 * @return a new String that is composed of the elements separated by the delimiter
	 */
	public static String join(final String delimiter, final Object... objects) {
		final StringBuilder stringbuilder = new StringBuilder();
		final Iterable<String> iterable = new ArrayList<String>();

		for (final Object o : objects) {
			final String s = StringUtils.getString(o);
			if (!Strings.isBlankString(s)) {

			}
		}

		if (Strings.isBlankString(delimiter)) {
			for (final Object o : objects) {
				stringbuilder.append(o.toString());
			}

			return stringbuilder.toString();

		} else {
			for (final Object o : objects) {
				stringbuilder.append(o.toString() + delimiter);
			}

			return stringbuilder.substring(0, stringbuilder.lastIndexOf(delimiter));
		}
	}


	/**
	 * Joins elements into a String using a specified delimiter.
	 * 
	 * Concatenates the string values of elements from collection using a specified delimiter
	 * 
	 * @param source
	 *        Collection to join
	 * 
	 * @param delimiter
	 *        String used to delimit elements in source
	 * 
	 * @return String values of all elements in source concatenated by delimiter
	 */
	@SuppressWarnings( { "rawtypes", "cast" })
	public static String join(final Collection source, final String delimiter) {
		if ((null != source) && (source.size() > 0)) {
			final StringBuilder stringbuilder = new StringBuilder();
			if (source.iterator().next() instanceof Object) {
				// treat as an object
				for (final Object o : source) {
					stringbuilder.append(o.toString() + delimiter);
				}
			} else {
				// treat as a primitive
				final Iterator it = source.iterator();
				while (it.hasNext()) {
					stringbuilder.append(String.valueOf(it.next()) + delimiter);
				}
			}

			return stringbuilder.substring(0, stringbuilder.lastIndexOf(delimiter));
		}

		return "";
	}


	public static String join(final String[] source, final String delimiter) {
		if ((null != source) && (source.length > 0)) {
			final StringBuilder stringbuilder = new StringBuilder();

			for (int i = 0; i < source.length; i++) {
				stringbuilder.append(source[i]);
				if (i < source.length) {
					stringbuilder.append(delimiter);
				}
			}

			return stringbuilder.toString();
		}
		return "";
	}


	/**
	 * Joins elements into a String using a specified delimiter.
	 * 
	 * Concatenates the string values of elements from an array or collection using a specified delimiter
	 * 
	 * @param source
	 *        Array or Collection to join
	 * 
	 * @param delimiter
	 *        String used to delimit elements
	 * 
	 * @return String values of all elements in source concatenated by delimiter
	 */
	@SuppressWarnings("rawtypes")
	public static String join(final Object source, final String delimiter) {
		if (null != source) {
			if (source instanceof Collection) { return StringUtils.join((Collection) source, delimiter); }

			final String classname = source.getClass().getName();
			if (classname.equalsIgnoreCase("java.lang.String[]") || classname.equalsIgnoreCase("[Ljava.lang.String;")) {
				final StringBuilder stringbuilder = new StringBuilder();
				if (Strings.isBlankString(delimiter)) {
					for (final String s : (String[]) source) {
						stringbuilder.append(s + delimiter);
					}
					return stringbuilder.toString();

				} else {

					for (final String s : (String[]) source) {
						stringbuilder.append(s + delimiter);
					}
					return stringbuilder.substring(0, stringbuilder.lastIndexOf(delimiter));
				} // if (Strings.isBlankString(delimiter))

			} else {
				return StringUtils.join(CollectionUtils.getStringArray(source), delimiter);
			}
		}

		return "";
	}


	/**
	 * Gets the String of an object WITHOUT THROWING AN EXCEPTION.
	 * 
	 * Handles null and enum instances.
	 * 
	 * Null is returned as an empty string "", Enum instances return the name of the enum. If the object is an instance of String, it will
	 * be cast as a String and returned. Otherwise, the result of the object's toString() method will be returned.
	 * 
	 * @param source
	 *        object for which to return the String.
	 * 
	 * @return String representation the object. Empty string "" on exception.
	 */
	public static String getString(final Object source) {
		return StringUtils.getString(source, "");
	}


	/**
	 * Gets the String of an object WITHOUT THROWING AN EXCEPTION.
	 * 
	 * Handles null and enum instances.
	 * 
	 * Null is returned as an empty string "", Enum instances return the name of the enum. If the object is an instance of String, it will
	 * be cast as a String and returned. Otherwise, the result of the object's toString() method will be returned.
	 * 
	 * @param source
	 *        object for which to return the String.
	 * 
	 * @param delimiter
	 *        Delimiter to use between collection members.
	 * 
	 * 
	 * @return String representation the object. Empty string "" on exception.
	 */
	@SuppressWarnings("unchecked")
	public static String getString(final Object source, final String delimiter) {
		// TODO Talk to NTF about moving this to ODA org.openntf.domino.utils.Strings
		if (null == source) { return ""; }

		try {
			// Enums
			if (source instanceof Enum<?>) { return ((Enum<?>) source).name(); }

			// Enumerations
			if (source instanceof Enumeration) {
				final Collection<String> list = new ArrayList<String>();
				final Enumeration enumeration = (Enumeration) source;
				while (enumeration.hasMoreElements()) {
					list.add(StringUtils.getString(enumeration.nextElement()));
				}

				return Strings.join(list, (Strings.isBlankString(delimiter)) ? "" : delimiter);
			}

			// Maps
			if (source instanceof Map) {
				final Collection<String> list = new ArrayList<String>();

				final Iterator<Map.Entry<Object, Object>> it = (((Map) source).entrySet().iterator());
				while (it.hasNext()) {
					final Map.Entry<Object, Object> entry = it.next();
					list.add(StringUtils.getString(entry.getKey(), "").concat("=").concat(StringUtils.getString(entry.getValue(), "")));
				}

				return Strings.join(list, (Strings.isBlankString(delimiter)) ? "" : delimiter);
			}

			// Collections
			if (source instanceof Collection) {
				final Object[] array = ((Collection) source).toArray();
				if (array.length < 1) { return ""; }

				final Collection<String> list = new ArrayList<String>();
				for (final Object element : array) {
					list.add(StringUtils.getString(element, ""));
				}

				return Strings.join(list, (Strings.isBlankString(delimiter)) ? "" : delimiter);
			}

			// Arrays
			if (source.getClass().isArray()) { return Arrays.deepToString((Object[]) source); }

			// everything else
			return (source instanceof String) ? (String) source : String.valueOf(source);

		} catch (final Exception e) {
			// do nothing
		}

		return "";
	}

}
