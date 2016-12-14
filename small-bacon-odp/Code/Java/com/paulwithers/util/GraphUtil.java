package com.paulwithers.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openntf.domino.Database;
import org.openntf.domino.graph2.DElementStore;
import org.openntf.domino.graph2.impl.DConfiguration;
import org.openntf.domino.graph2.impl.DFramedGraphFactory;
import org.openntf.domino.graph2.impl.DFramedTransactionalGraph;
import org.openntf.domino.graph2.impl.DGraph;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

import com.bacon.model.Actor;
import com.bacon.model.Movie;

public class GraphUtil {
	private static DFramedTransactionalGraph GRAPH_INSTANCE;

	public static enum Store {
		DEFAULT, ACTORS, MOVIES;
	}

	protected static Map<Store, String> DEFAULT_PATH_MAPPINGS;
	static {
		Map<Store, String> map = new EnumMap<Store, String>(Store.class);
		// map.put(Store.BOOKREVIEWS, "Training/BOOKREV.NSF");
		map.put(Store.ACTORS, "iconuk/smalldemo/actors.nsf");
		map.put(Store.MOVIES, "iconuk/smalldemo/movies.nsf");
		map.put(Store.DEFAULT, "iconuk/smalldemo/edges.nsf");
		DEFAULT_PATH_MAPPINGS = map;
	}

	private Map<Store, String> pathMap_;

	public static synchronized DFramedTransactionalGraph getGraphInstance() {
		if (GRAPH_INSTANCE == null) {
			GRAPH_INSTANCE = initGraph();
		}
		return GRAPH_INSTANCE;
	}

	public static void nukeData() {
		for (Store storeName : DEFAULT_PATH_MAPPINGS.keySet()) {
			Database db = Factory.getSession(SessionType.NATIVE).getDatabase(DEFAULT_PATH_MAPPINGS.get(storeName));
			db.getAllDocuments().removeAll(true);
		}
	}

	protected static DFramedTransactionalGraph initGraph() {
		DConfiguration config = new DConfiguration();
		DGraph graph = new DGraph(config);
		Map<Store, DElementStore> storeMap = getElementStores();
		for (Entry<Store, DElementStore> entry : storeMap.entrySet()) {
			config.addElementStore(entry.getValue());
			if (entry.getKey() == Store.DEFAULT) {
				config.setDefaultElementStore(entry.getValue().getStoreKey());
			}
		}
		DFramedGraphFactory factory = new DFramedGraphFactory(config);
		return (DFramedTransactionalGraph) factory.create(graph);
	}

	protected static Map<Store, DElementStore> getElementStores() {
		Map<Store, DElementStore> result = new EnumMap<Store, DElementStore>(Store.class);
		Map<Store, String> pathMap = DEFAULT_PATH_MAPPINGS;

		DElementStore actorStore = new org.openntf.domino.graph2.impl.DElementStore();
		result.put(Store.ACTORS, actorStore);
		actorStore.addType(Actor.class);
		actorStore.setStoreKey(pathMap.get(Store.ACTORS));

		DElementStore movieStore = new org.openntf.domino.graph2.impl.DElementStore();
		result.put(Store.MOVIES, movieStore);
		movieStore.addType(Movie.class);
		movieStore.setStoreKey(pathMap.get(Store.MOVIES));

		DElementStore defaultStore = new org.openntf.domino.graph2.impl.DElementStore();
		defaultStore.setStoreKey(pathMap.get(Store.DEFAULT));
		result.put(Store.DEFAULT, defaultStore);

		return result;
	}

}
