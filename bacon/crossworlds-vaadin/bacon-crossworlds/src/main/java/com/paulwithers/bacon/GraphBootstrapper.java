package com.paulwithers.bacon;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.openntf.domino.graph2.DElementStore;
import org.openntf.domino.graph2.impl.DConfiguration;
import org.openntf.domino.graph2.impl.DFramedGraphFactory;
import org.openntf.domino.graph2.impl.DGraph;
import org.openntf.xworlds.appservers.webapp.XWorldsApplicationListener;

import com.paulwithers.bacon.model.Actor;
import com.paulwithers.bacon.model.Movie;
import com.tinkerpop.frames.FramedGraph;

@WebListener
public class GraphBootstrapper extends XWorldsApplicationListener {
	private static GraphBootstrapper INSTANCE;
	private static FramedGraph GRAPH_INSTANCE;

	public static enum Store {
		DEFAULT, ACTORS, MOVIES;
	}

	protected static Map<Store, String> DEFAULT_PATH_MAPPINGS;
	static {
		Map<Store, String> map = new EnumMap<Store, String>(Store.class);
		// map.put(Store.BOOKREVIEWS, "Training/BOOKREV.NSF");
		map.put(Store.ACTORS, "demo/actors.nsf");
		map.put(Store.MOVIES, "demo/movies.nsf");
		map.put(Store.DEFAULT, "demo/edges.nsf");
		DEFAULT_PATH_MAPPINGS = map;
	}

	private Map<Store, String> pathMap_;

	public GraphBootstrapper() {
		if (INSTANCE == null) {
			INSTANCE = this;
		} else {
			throw new IllegalStateException("Cannot instantiate more than one GraphFactory instance!");
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent appEvent) {
		super.contextInitialized(appEvent);
		getGraphInstance();
	}

	@Override
	public void contextDestroyed(ServletContextEvent appEvent) {
		super.contextDestroyed(appEvent);
	}

	public static synchronized GraphBootstrapper getInstance() {
		if (INSTANCE == null) {
			return new GraphBootstrapper();
		}
		return INSTANCE;
	}

	public synchronized FramedGraph getGraphInstance() {
		if (GRAPH_INSTANCE == null) {
			GRAPH_INSTANCE = initGraph();
		}
		return GRAPH_INSTANCE;
	}

	public synchronized Iterable<Actor> getActors() {
		return getGraphInstance().getVertices(null, null, Actor.class);
	}

	protected synchronized FramedGraph<DGraph> initGraph() {
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
		return factory.create(graph);
	}

	protected Map<Store, DElementStore> getElementStores() {
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
