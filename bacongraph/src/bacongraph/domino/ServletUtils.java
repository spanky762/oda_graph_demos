package bacongraph.domino;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.azlw.util.StringUtils;
import org.openntf.domino.rest.service.ODAGraphService;
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

	public static enum HttpServletRequestProperty {
		ATTRIBUTENAMES,
		AUTHTYPE,
		CHARACTERENCODING,
		CONTENTLENGTH,
		CONTENTTYPE,
		CONTEXTPATH,
		COOKIES,
		HEADERNAMES,
		ISREQUESTEDSESSIONIDFROMCOOKIE,
		ISREQUESTEDSESSIONIDFROMURL,
		ISSECURE,
		LOCALADDR,
		LOCALE,
		LOCALES,
		LOCALNAME,
		LOCALPORT,
		METHOD,
		PARAMETERMAP,
		PARAMETERNAMES,
		PATHINFO,
		PATHTRANSLATED,
		PROTOCOL,
		QUERYSTRING,
		REMOTEADDR,
		REMOTEHOST,
		REMOTEUSER,
		REQUESTEDSESSIONID,
		REQUESTHOST,
		REQUESTURI,
		REQUESTURL,
		SCHEME,
		SERVERNAME,
		SERVERPORT,
		SERVLETPATH,
		SESSION,
		USERPRINCIPAL;

		/**
		 * Gets the HttpServletRequestProperty instance for the specified key
		 * 
		 * @param key
		 *        Key specifying which instance should be returned.
		 * 
		 * @return HttpServletRequestProperty instance, or null if key is invalid.
		 */
		public static HttpServletRequestProperty getValue(final Object key) {
			if (null == key) { return null; }
			if (key instanceof HttpServletRequestProperty) { return (HttpServletRequestProperty) key; }

			final String ucKey = StringUtils.toUpperCase(key);
			if (Strings.isBlankString(ucKey)) { return null; }

			try {
				final HttpServletRequestProperty result = HttpServletRequestProperty.valueOf(ucKey);
				if (null != result) { return result; }
			} catch (final Exception e) {
				// do nothing
			}

			return null;
		}
	}


	/**
	 * Gets the HttpServletRequest for the ExternalContext.
	 * 
	 * @return the HttpServletRequest of ExternalContext;
	 */
	public static HttpServletRequest getHttpServletRequest() {

		return ODAGraphService.getCurrentRequest();

	}


	public static Object getHttpServletRequestProperty(final HttpServletRequestProperty key) {
		if (null == key) { return null; }

		try {
			final HttpServletRequest r = ODAGraphService.getCurrentRequest();

			switch (key) {
				case ATTRIBUTENAMES:
					return r.getAttributeNames();

				case AUTHTYPE:
					return r.getAuthType();

				case CHARACTERENCODING:
					return r.getCharacterEncoding();

				case CONTENTLENGTH:
					return r.getContentLength();

				case CONTENTTYPE:
					return r.getContentType();

				case CONTEXTPATH:
					return r.getContextPath();

				case COOKIES:
					return r.getCookies();

				case HEADERNAMES:
					return r.getHeaderNames();

				case ISREQUESTEDSESSIONIDFROMCOOKIE:
					return r.isRequestedSessionIdFromCookie();

				case ISREQUESTEDSESSIONIDFROMURL:
					return r.isRequestedSessionIdFromURL();

				case ISSECURE:
					return r.isSecure();

				case LOCALADDR:
					return r.getLocalAddr();

				case LOCALE:
					return r.getLocale();

				case LOCALES:
					return r.getLocales();

				case LOCALNAME:
					return r.getLocalName();

				case LOCALPORT:
					return r.getLocalPort();

				case METHOD:
					return r.getMethod();

				case PARAMETERMAP:
					return r.getParameterMap();

				case PARAMETERNAMES:
					return r.getParameterNames();

				case PATHINFO:
					return r.getPathInfo();

				case PATHTRANSLATED:
					return r.getPathTranslated();

				case PROTOCOL:
					return r.getProtocol();

				case QUERYSTRING:
					return r.getQueryString();

				case REMOTEADDR:
					return r.getRemoteAddr();

				case REMOTEHOST:
					return r.getRemoteHost();

				case REMOTEUSER:
					return r.getRemoteUser();

				case REQUESTEDSESSIONID:
					return r.getRequestedSessionId();

				case REQUESTHOST:
					return ServletUtils.getRequestHost(r);

				case REQUESTURI:
					return r.getRequestURI();

				case REQUESTURL:
					return r.getRequestURL();

				case SCHEME:
					return r.getScheme();

				case SERVERNAME:
					return r.getServerName();

				case SERVERPORT:
					return r.getServerPort();

				case SERVLETPATH:
					return r.getServletPath();

				case SESSION:
					return r.getSession();

				case USERPRINCIPAL:
					return r.getUserPrincipal();

				default:
					return null;
			}

		} catch (final Exception e) {
			ServletUtils.handleException(e, true, "getHttpServletRequestProperty()", "HttpServletRequestProperty: " + key);
			throw new RuntimeException((null == e.getCause()) ? e : e.getCause());
		}
	}


	public static String getRequestHost(final HttpServletRequest request) {
		if (null == request) { return ""; }

		final StringBuffer url = request.getRequestURL();
		final String uri = request.getRequestURI();
		return url.substring(0, url.indexOf(uri));
	}


	public static String getRequestURL() {
		return StringUtils.getString(ServletUtils.getHttpServletRequestProperty(HttpServletRequestProperty.REQUESTURL));
	}


	/**
	 * Gets the QueryString of the current HttpServletRequest
	 * 
	 * @return the HttpServletRequest QueryString
	 */
	public static String getQueryString() {
		final HttpServletRequest httpServletRequest = ServletUtils.getHttpServletRequest();
		return (null == httpServletRequest) ? "" : httpServletRequest.getQueryString();
	}


	/**
	 * Gets the QueryString Arguments as a map of key-value pairs
	 * 
	 * @return Map of key-value pairs for the current QueryString
	 */
	public static Map<String, String> getQueryStringArguments() {
		return ServletUtils.getQueryStringArguments(ServletUtils.getQueryString());
	}


	/**
	 * Gets the QueryString Arguments as a map of key-value pairs
	 * 
	 * @return Map of key-value pairs for the current QueryString
	 */
	public static Map<String, String> getQueryStringArgumentsRaw() {
		return ServletUtils.getQueryStringArgumentsRaw(ServletUtils.getQueryString());
	}


	/**
	 * Gets the QueryString Arguments as a map of key-value pairs
	 * 
	 * @param queryString
	 *        from which to generate the map.
	 * 
	 * @return Map of key-value pairs
	 */
	public static Map<String, String> getQueryStringArguments(final String queryString) {
		final Map<String, String> result = new HashMap<String, String>();
		final Iterator<Map.Entry<String, String>> it = ServletUtils.getQueryStringArgumentsRaw(queryString).entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<String, String> entry = it.next();
			result.put(entry.getKey().toUpperCase(), entry.getValue());
		}

		return result;
	}


	/**
	 * Gets the QueryString Arguments as a map of key-value pairs
	 * 
	 * @param queryString
	 *        from which to generate the map.
	 * 
	 * @return Map of key-value pairs
	 */
	public static Map<String, String> getQueryStringArgumentsRaw(final String queryString) {
		final Map<String, String> result = new HashMap<String, String>();
		String qs = queryString;
		if (Strings.isBlankString(qs)) { return result; }
		if (qs.startsWith("?")) {
			qs = qs.substring(1);
		}
		if (qs.startsWith("&")) {
			qs = qs.substring(1);
		}

		if (Strings.isBlankString(qs)) { return result; }

		String[] pairs = null;
		if (qs.indexOf('&') > -1) {
			// Split the String into component parts
			pairs = qs.split("&");
		} else {
			pairs = new String[] { qs };
		}

		// Process each pair and add to the result
		for (final String pair : pairs) {
			final int idx = pair.indexOf('=');
			if (idx > 0) {

				final String key = pair.substring(0, idx);
				final String value = pair.substring(idx + 1);
				if (!Strings.isBlankString(key)) {
					result.put(key, (Strings.isBlankString(value)) ? "" : value);
				}
			}
		}

		return result;
	}


	/**
	 * Gets the Method of the current HttpServletRequest
	 * 
	 * @return the HttpServletRequest Method
	 */
	public static String getHttpServletRequestMethod() {
		final HttpServletRequest httpServletRequest = ServletUtils.getHttpServletRequest();
		return (null == httpServletRequest) ? "" : httpServletRequest.getMethod();
	}


	/**
	 * Spawns a QueryString from a map of key-value pairs.
	 * 
	 * @param arguments
	 *        Map of key-value pairs from which to generate the QueryString.
	 * 
	 * @return QueryString constructed from mapped key-value pairs.
	 */
	public static String spawnQueryString(final Map<String, String> arguments) {
		final StringBuilder sb = new StringBuilder();

		if ((null != arguments) && !arguments.isEmpty()) {
			final Iterator<Map.Entry<String, String>> it = arguments.entrySet().iterator();
			while (it.hasNext()) {
				final Map.Entry<String, String> entry = it.next();
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
				if (it.hasNext()) {
					sb.append("&");
				}
			}

		}

		return sb.toString();
	}


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
