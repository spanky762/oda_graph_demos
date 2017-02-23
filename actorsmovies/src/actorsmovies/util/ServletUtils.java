package actorsmovies.util;

import java.util.ArrayList;
import java.util.List;

import org.openntf.domino.utils.Strings;

/**
 * Servlet Utilities.
 * 
 * TODO: The Utility methods in this class should eventually be added to
 * org.openntf.domino.utils.XSPUtils Once that has been completed and rolled out
 * this will be marked as deprecated.
 * 
 * @author Devin S. Olson (devin@redpillnow.com)
 * 
 */
public enum ServletUtils {
	; // NO INSTANCE MEMBERS

	public static void handleException(final Exception e, final boolean includeStackTrace) {
		if (null == e) { return; }
		ServletUtils.handleException(e, includeStackTrace, new ArrayList<String>());
	}


	public static void handleException(final Exception e, final boolean includeStackTrace, final Object... args) {
		if (null == e) { return; }

		final List<String> strings = new ArrayList<String>();
		if (null != args) {
			for (final Object arg : args) {
				if (null != arg) {
					strings.add(StringUtils.getString(arg, ", "));
				}
			}
		}

		ServletUtils.handleException(e, includeStackTrace, strings);
	}


	public static void handleException(final Exception e, final boolean includeStackTrace, final List<String> strings) {
		if (null == e) { return; }
		if (null == strings) {
			ServletUtils.handleException(e, includeStackTrace, new ArrayList<String>());
			return;
		}

		System.out.println("  ");
		System.out.println("******************************");
		System.out.println("EXCEPTION ");
		System.out.println(e.toString());

		if (!includeStackTrace) {
			final StackTraceElement[] elements = e.getStackTrace();
			if (null != elements) {
				int i = 0;
				while (i < 5) {
					if (elements.length > i) {
						System.out.println("\t at ".concat(elements[i].toString()));
					}
					i++;
				}

				if (elements.length > i) {
					System.out.println("\t " + (elements.length - i) + " more...");
				}
			}
		}

		if (!strings.isEmpty()) {
			System.out.println("  ");
			for (final String s : strings) {
				if (!Strings.isBlankString(s)) {
					System.out.println("\t".concat(s));
				}
			}
		}

		System.out.println("******************************");

		if (includeStackTrace) {
			System.out.println("Stack Trace: ");
			e.printStackTrace();
		}

		System.out.println(" ");
	}


	/**
	 * Writes output to the console. Includes the fully qualified name of
	 * object.
	 * 
	 * @param type
	 *        The class calling this method.
	 * 
	 * @param method
	 *        Method to append to the object name.
	 * 
	 * @param consoleText
	 *        Text to write to the console.
	 */
	public static void staticConsole(final Class<? extends Object> type, final String method, final String consoleText) {
		final StringBuilder sb = new StringBuilder();

		if (null != type) {
			sb.append(type.getName());
		}

		if (!Strings.isBlankString(method)) {
			sb.append(".");
			sb.append(method);
		}

		sb.append("\t");
		sb.append(consoleText);

		System.out.println(sb.toString());
	}

}
