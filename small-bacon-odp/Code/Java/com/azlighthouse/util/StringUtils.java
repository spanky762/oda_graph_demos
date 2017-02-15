package com.azlighthouse.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openntf.domino.utils.CollectionUtils;
import org.openntf.domino.utils.Strings;

public enum StringUtils {
	; // NO INSTANCE MEMBERS


	/**
	 * Returns a new String composed of copies of the String elements joined
	 * together with a copy of the specified delimiter.
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
	 * Note that if an element is null, then it will not be included in the
	 * result.
	 * 
	 * @param delimiter
	 *            the delimiter that separates each element
	 * @param elements
	 *            an Iterable that will have its elements joined together.
	 * 
	 * @return a new String that is composed from the elements argument
	 */
	public static String join(final String delimiter, final Iterable<? extends String> elements) {
		if (null == elements) { return ""; }

		StringBuilder sb = new StringBuilder();
		String d = (Strings.isBlankString(delimiter)) ? "" : delimiter;
		Iterator<? extends String> it = elements.iterator();
		boolean hasContent = false;
		while (it.hasNext()) {
			String s = it.next();

			if (!Strings.isBlankString(s)) {
				if (hasContent) sb.append(d);

				sb.append(s);
				hasContent = true;
			}
		}

		return sb.toString();
	}


	/**
	 * Returns a new String composed of copies of the String elements joined
	 * together with a copy of the specified delimiter.
	 * 
	 * For example,
	 * 
	 * String message = String.join("-", "Java", "is", "cool"); // message
	 * returned is: "Java-is-cool"
	 * 
	 * Note that if an element is null, then it will not be included in the
	 * result.
	 * 
	 * @param delimiter
	 *            the delimiter that separates each element
	 * @param elements
	 *            the elements to join together.
	 * 
	 * @return a new String that is composed of the elements separated by the
	 *         delimiter
	 */
	public static String join(final String delimiter, final String... elements) {
		// signature also valid for join(String, String[])
		if (null == elements) { return ""; }

		final List<String> strings = new ArrayList<String>();
		for (final String string : elements) {
			if (!Strings.isBlankString(string)) {
				strings.add(string);
			}
		}

		return StringUtils.join(delimiter, strings);
	}


	/**
	 * Returns a new String composed of String values of the Object(s) joined
	 * together with a copy of the specified delimiter.
	 * 
	 * Note that if an element is null, then it will not be included in the
	 * result.
	 * 
	 * @param delimiter
	 *            the delimiter that separates each element
	 * @param objects
	 *            the objects to join together.
	 * 
	 * @return a new String that is composed of the elements separated by the
	 *         delimiter
	 */
	public static String join(final String delimiter, final Object... objects) {
		final List<String> strings = new ArrayList<String>();

		for (final Object o : objects) {
			final String s = StringUtils.getString(o);
			if (!Strings.isBlankString(s)) {
				strings.add(s);
			}
		}

		return StringUtils.join(delimiter, strings);
	}


	/**
	 * Joins elements into a String using a specified delimiter.
	 * 
	 * Concatenates the string values of elements from an array or collection
	 * using a specified delimiter
	 * 
	 * @param source
	 *            Array or Collection to join
	 * 
	 * @param delimiter
	 *            String used to delimit elements
	 * 
	 * @return String values of all elements in source concatenated by delimiter
	 */
	@SuppressWarnings("unchecked")
	public static String join(final String delimiter, final Object source) {
		if (null == source) { return ""; }
		if (source instanceof Collection) { return StringUtils.join(delimiter, (Collection) source); }

		final String classname = source.getClass().getName();
		if (classname.equalsIgnoreCase("java.lang.String[]") || classname.equalsIgnoreCase("[Ljava.lang.String;")) {
			List<String> list = new ArrayList<String>();
			for (final String s : (String[]) source) {
				list.add(s);
			}

			return StringUtils.join(delimiter, list);

		} else {
			return StringUtils.join(delimiter, CollectionUtils.getStringArray(source));
		}
	}


	/**
	 * Concatenates the string values of elements from a collection using a
	 * specified delimiter
	 * 
	 * Note that if an element is null, then it will not be included in the
	 * result.
	 * 
	 * @param delimiter
	 *            the delimiter that separates each element
	 * @param collection
	 *            the objects to join together.
	 * 
	 * @return a new String that is composed of the Collection elements
	 *         separated by the delimiter
	 */
	@SuppressWarnings("unchecked")
	public static String joinCollection(final String delimiter, final Collection source) {
		if ((null == source) || (source.size() < 1)) { return ""; }

		final List<String> strings = new ArrayList<String>();
		if (source.iterator().next() instanceof Object) {
			// treat as an object
			for (final Object o : source) {
				strings.add(StringUtils.getString(o));
			}
		} else {
			// treat as a primitive
			final Iterator it = source.iterator();
			while (it.hasNext()) {
				strings.add(String.valueOf(it.next()));
			}
		}

		return StringUtils.join(delimiter, strings);
	}


	/**
	 * @deprecated - use {@link #joinCollection(String, Collection)}
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public static String join(final Collection source, final String delimiter) {
		return StringUtils.join(delimiter, source);
	}

	/**
	 * @deprecated - use {@link #join(String, String...)}
	 */
	@Deprecated
	public static String join(final String[] source, final String delimiter) {
		return StringUtils.join(delimiter, source);
	}


	/**
	 * @deprecated - use {@link #join(String, Object)}
	 */
	@Deprecated
	public static String join(final Object source, final String delimiter) {
		return StringUtils.join(delimiter, source);
	}


	/**
	 * Gets the String of an object WITHOUT THROWING AN EXCEPTION.
	 * 
	 * Handles null and enum instances.
	 * 
	 * Null is returned as an empty string "", Enum instances return the name of
	 * the enum. If the object is an instance of String, it will be cast as a
	 * String and returned. Otherwise, the result of the object's toString()
	 * method will be returned.
	 * 
	 * @param source
	 *            object for which to return the String.
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
	 * Null is returned as an empty string "", Enum instances return the name of
	 * the enum. If the object is an instance of String, it will be cast as a
	 * String and returned. Otherwise, the result of the object's toString()
	 * method will be returned.
	 * 
	 * @param source
	 *            object for which to return the String.
	 * 
	 * @param delimiter
	 *            Delimiter to use between collection members.
	 * 
	 * 
	 * @return String representation the object. Empty string "" on exception.
	 */
	@SuppressWarnings("unchecked")
	public static String getString(final Object source, final String delimiter) {
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

				return StringUtils.join((Strings.isBlankString(delimiter)) ? "" : delimiter, list);
			}

			// Maps
			if (source instanceof Map) {
				final Collection<String> list = new ArrayList<String>();

				final Iterator<Map.Entry<Object, Object>> it = (((Map) source).entrySet().iterator());
				while (it.hasNext()) {
					final Map.Entry<Object, Object> entry = it.next();
					list.add(StringUtils.getString(entry.getKey(), "").concat("=").concat(StringUtils.getString(entry.getValue(), "")));
				}

				return StringUtils.join((Strings.isBlankString(delimiter)) ? "" : delimiter, list);
			}

			// Collections
			if (source instanceof Collection) {
				final Object[] array = ((Collection) source).toArray();
				if (array.length < 1) { return ""; }

				final Collection<String> list = new ArrayList<String>();
				for (final Object element : array) {
					list.add(StringUtils.getString(element, ""));
				}

				return StringUtils.join((Strings.isBlankString(delimiter)) ? "" : delimiter, list);
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
