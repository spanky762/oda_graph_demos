package com.paulwithers.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openntf.domino.Database;
import org.openntf.domino.graph2.DElementStore;
import org.openntf.domino.graph2.impl.DConfiguration;
import org.openntf.domino.graph2.impl.DFramedGraphFactory;
import org.openntf.domino.graph2.impl.DFramedTransactionalGraph;
import org.openntf.domino.graph2.impl.DGraph;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Strings;
import org.openntf.domino.utils.Factory.SessionType;

import com.azlighthouse.util.ServletUtils;
import com.azlighthouse.util.StringUtils;
import com.bacon.model.Actor;
import com.bacon.model.Movie;

public class GraphUtil {
	private static DFramedTransactionalGraph	GRAPH_INSTANCE;

	private static final String					FILEPATH_BASE	= "demos/bacon/small/";

	private static enum StoreKind {
		DEFAULT(Object.class, FILEPATH_BASE.concat("edges.nsf")),
		ACTORS(Actor.class),
		MOVIES(Movie.class);

		private final Class<? extends Object>	_type;
		private final String					_path;

		private StoreKind(final Class<? extends Object> type) {
			this._type = type;
			this._path = FILEPATH_BASE.concat(this.name().toLowerCase().concat(".nsf"));
		}

		private StoreKind(final Class<? extends Object> type, String path) {
			this._type = type;
			this._path = (Strings.isBlankString(path)) ? FILEPATH_BASE.concat(this.name().toLowerCase().concat(".nsf")) : path;
		}


		public Class<? extends Object> getType() {
			return this._type;
		}

		public String getPath() {
			return this._path;
		}
	}


	public static synchronized DFramedTransactionalGraph getGraphInstance() {
		if (GRAPH_INSTANCE == null) {
			GRAPH_INSTANCE = GraphUtil.initGraph();
		}
		return GRAPH_INSTANCE;
	}


	/**
	 * Writes output to the console. Includes the fully qualified name of
	 * object.
	 * 
	 * Calls CzarDebug to write output to the console.
	 * 
	 * @param method
	 *            Method to append to the object name.
	 * 
	 * @param consoleText
	 *            Text to write to the console.
	 */
	protected void console(final String method, final String consoleText) {
		final StringBuilder sb = new StringBuilder(this.getClass().getName());
		if (!Strings.isBlankString(method)) {
			sb.append(".");
			sb.append(method);
		}

		sb.append("\t ");
		sb.append(consoleText);
		System.out.println(sb.toString());
	}


	protected void handleException(final Exception e, final boolean includeStackTrace, final Object... args) {
		if (null == e) { return; }
		try {

			final List<String> strings = new ArrayList<String>();
			if (null != args) {
				for (final Object arg : args) {
					if (null != arg) {
						strings.add(StringUtils.getString(arg, ", "));
					}
				}
			}

			ServletUtils.handleException(e, includeStackTrace, strings);

		} catch (final Exception e1) {
			ServletUtils.handleException(e, includeStackTrace, args);
		}
	}


	public static void nukeData() {
		try {
			for (StoreKind storekind : StoreKind.values()) {
				Database db = Factory.getSession(SessionType.NATIVE).getDatabase(storekind.getPath());
				db.getAllDocuments().removeAll(true);
			}

		} catch (Exception e) {
			ServletUtils.handleException(e, true);
		}
	}

	protected static DFramedTransactionalGraph initGraph() {
		DConfiguration config = new DConfiguration();
		DGraph graph = new DGraph(config);
		Map<StoreKind, DElementStore> storeMap = GraphUtil.getElementStores();
		for (Entry<StoreKind, DElementStore> entry : storeMap.entrySet()) {
			config.addElementStore(entry.getValue());
		}

		config.setDefaultElementStore(storeMap.get(StoreKind.DEFAULT).getStoreKey());

		DFramedGraphFactory factory = new DFramedGraphFactory(config);
		return (DFramedTransactionalGraph) factory.create(graph);
	}


	protected static Map<StoreKind, DElementStore> getElementStores() {
		Map<StoreKind, DElementStore> result = new EnumMap<StoreKind, DElementStore>(StoreKind.class);

		for (StoreKind storekind : EnumSet.of(StoreKind.ACTORS, StoreKind.MOVIES)) {
			DElementStore store = new org.openntf.domino.graph2.impl.DElementStore();
			store.addType(storekind.getType());
			store.setStoreKey(storekind.getPath());
			result.put(storekind, store);
		}

		DElementStore defaultStore = new org.openntf.domino.graph2.impl.DElementStore();
		defaultStore.setStoreKey(StoreKind.DEFAULT.getPath());
		result.put(StoreKind.DEFAULT, defaultStore);
		return result;
	}
}
