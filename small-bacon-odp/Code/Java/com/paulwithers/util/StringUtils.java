package com.paulwithers.util;

import java.util.Collection;
import java.util.Iterator;

import org.openntf.domino.utils.CollectionUtils;
import org.openntf.domino.utils.Strings;

public enum StringUtils {
	; // NO INSTANCE MEMBERS

	/**
	 * Returns a new String composed of copies of the CharSequence elements joined together with a copy of the specified delimiter.
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
	public static String join(final CharSequence delimiter, final CharSequence... elements) {
		return null;
	}


	/**
	 * Returns a new String composed of copies of the CharSequence elements joined together with a copy of the specified delimiter.
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
	public static String join(final CharSequence delimiter, final Iterable<? extends CharSequence> elements) {
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
	public static String join(final CharSequence delimiter, final Object... objects) {
		return null;
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
	 * Concatenates the string values of elements from collection using a specified delimiter
	 * 
	 * @param delimiter
	 *        String used to delimit elements in source
	 * 
	 * @param objects
	 *        Objects to join
	 * 
	 * @return String values of all elements in source concatenated by delimiter
	 */
	public static String join(final String delimiter, final Object... objects) {
		final StringBuilder stringbuilder = new StringBuilder();
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

}
