package com.paulwithers.util;

import java.util.EnumMap;
import java.util.EnumSet;
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

import com.bacon.model.Actor;
import com.bacon.model.Movie;

public class GraphUtil {
	private static DFramedTransactionalGraph	GRAPH_INSTANCE;


	private static final String					FILEPATH_BASE	= "demos/bacon/big/";

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

	// public static enum Store {
	// DEFAULT,
	// ACTORS,
	// MOVIES;
	// }
	//
	// protected static Map<Store, String> DEFAULT_PATH_MAPPINGS;
	// static {
	// Map<Store, String> map = new EnumMap<Store, String>(Store.class);
	// map.put(Store.ACTORS, "demos/bacon/big/actors.nsf");
	// map.put(Store.MOVIES, "demos/bacon/big/movies.nsf");
	// map.put(Store.DEFAULT, "demos/bacon/big/edges.nsf");
	// DEFAULT_PATH_MAPPINGS = map;
	// }

	// private Map<Store, String> pathMap_;

	public static synchronized DFramedTransactionalGraph getGraphInstance() {
		if (GRAPH_INSTANCE == null) {
			GRAPH_INSTANCE = GraphUtil.initGraph();
		}
		return GRAPH_INSTANCE;
	}

	public static void nukeData() {
		for (StoreKind storekind : StoreKind.values()) {
			Database db = Factory.getSession(SessionType.NATIVE).getDatabase(storekind.getPath());
			db.getAllDocuments().removeAll(true);
		}
		// for (Store storeName : DEFAULT_PATH_MAPPINGS.keySet()) {
		// Database db = Factory.getSession(SessionType.NATIVE).getDatabase(DEFAULT_PATH_MAPPINGS.get(storeName));
		// db.getAllDocuments().removeAll(true);
		// }
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

	// protected static Map<Store, DElementStore> getElementStores() {
	// Map<Store, DElementStore> result = new EnumMap<Store, DElementStore>(Store.class);
	// Map<Store, String> pathMap = DEFAULT_PATH_MAPPINGS;
	//
	// DElementStore actorStore = new org.openntf.domino.graph2.impl.DElementStore();
	// result.put(Store.ACTORS, actorStore);
	// actorStore.addType(Actor.class);
	// actorStore.setStoreKey(pathMap.get(Store.ACTORS));
	//
	// DElementStore movieStore = new org.openntf.domino.graph2.impl.DElementStore();
	// result.put(Store.MOVIES, movieStore);
	// movieStore.addType(Movie.class);
	// movieStore.setStoreKey(pathMap.get(Store.MOVIES));
	//
	// DElementStore defaultStore = new org.openntf.domino.graph2.impl.DElementStore();
	// defaultStore.setStoreKey(pathMap.get(Store.DEFAULT));
	// result.put(Store.DEFAULT, defaultStore);
	//
	// return result;
	// }

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
