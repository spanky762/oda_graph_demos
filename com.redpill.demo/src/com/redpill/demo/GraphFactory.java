package com.redpill.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;

import org.openntf.domino.Session;
import org.openntf.domino.graph2.DConfiguration.IExtConfiguration;
import org.openntf.domino.graph2.DElementStore;
import org.openntf.domino.graph2.impl.DConfiguration;
import org.openntf.domino.graph2.impl.DFramedGraphFactory;
import org.openntf.domino.graph2.impl.DFramedTransactionalGraph;
import org.openntf.domino.graph2.impl.DGraph;
import org.openntf.domino.rest.resources.command.ICommandProcessor;
import org.openntf.domino.rest.service.IGraphFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xots.Xots;

import com.redpill.demo.model.Actor;
import com.redpill.demo.model.Movie;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedTransactionalGraph;

public class GraphFactory implements IGraphFactory {
	private static GraphFactory INSTANCE;
	private static FramedGraph DEFAULT_INSTANCE;
	private static List<Class<?>> callbackProcessorClasses_ = new ArrayList<Class<?>>();

	public static void addCommandProcessorCallback(Class<?> processorClass) {
		callbackProcessorClasses_.add(processorClass);
	}

	public static enum Store {
		DEFAULT, /* BOOKREVIEWS, */ACTORS, MOVIES;
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

	// private Map<String, ICommandProcessor> processorMap_ = new
	// HashMap<String, ICommandProcessor>();

	public GraphFactory() {
		// System.out.println("DEBUG: GraphFactory created");
		if (INSTANCE == null) {
			INSTANCE = this;
		} else {
			throw new IllegalStateException("Cannot instantiate more than one GraphFactory instance!");
		}
		if (!callbackProcessorClasses_.isEmpty()) {
			for (Class<?> klass : callbackProcessorClasses_) {
				try {
					ICommandProcessor proc = (ICommandProcessor) klass.newInstance();
					for (String namespace : proc.getNamespaces()) {
						for (String command : proc.getCommands()) {
							registerCommandProcessor(namespace, command, proc);
						}
					}
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public static class GraphInitializer implements Runnable {

		public final GraphFactory parent_;
		FramedTransactionalGraph result_;

		public GraphInitializer(GraphFactory parent) {
			parent_ = parent;
		}

		@Override
		public void run() {
			Session session = Factory.getSession(SessionType.NATIVE);
			Factory.setCurrentToSession(session);
			DConfiguration config = new DConfiguration();
			DGraph graph = new DGraph(config);
			Map<Store, DElementStore> storeMap = parent_.getInitialElementStores();
			for (Entry<Store, DElementStore> entry : storeMap.entrySet()) {
				config.addElementStore(entry.getValue());
				if (entry.getKey() == Store.DEFAULT) {
					config.setDefaultElementStore(entry.getValue().getStoreKey());
				}
			}
			DFramedGraphFactory factory = new DFramedGraphFactory(config);
			result_ = factory.create(graph);
			graph.setExtendedGraph(result_);
			session.switchSessionType(SessionType.CURRENT);
		}

		public FramedTransactionalGraph getResult() {
			return result_;
		}

	}

	public static synchronized GraphFactory getInstance() {
		if (INSTANCE == null) {
			return new GraphFactory();
		}
		return INSTANCE;
	}

	public synchronized FramedGraph getDefault() {
		if (DEFAULT_INSTANCE == null) {
			DEFAULT_INSTANCE = getGraph();
		}
		return DEFAULT_INSTANCE;
	}

	public Map<Store, String> getPathMap() {
		if (pathMap_ == null) {
			pathMap_ = DEFAULT_PATH_MAPPINGS;
		}
		return pathMap_;
	}

	public void setPathMap(Map<Store, String> pathMap) {
		pathMap_ = pathMap;
	}

	protected synchronized FramedGraph<DGraph> getGraph() {
		GraphInitializer init = new GraphInitializer(this);
		Xots.getService().execute(init);
		while (init.getResult() == null || Thread.interrupted()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("Initiatlizer interrupted. Can't get graph.");
			}
		}
		return init.getResult();
	}

	protected Map<Store, DElementStore> getInitialElementStores() {
		Map<Store, DElementStore> result = new EnumMap<Store, DElementStore>(Store.class);
		Map<Store, String> pathMap = getPathMap();

		// DElementStore bookReviewStore = new
		// org.openntf.domino.graph2.impl.DElementStore();
		// bookReviewStore.addType(Review.class);
		// bookReviewStore.addType(ViewVertex.class);
		// bookReviewStore.setStoreKey(pathMap.get(Store.BOOKREVIEWS));
		// result.put(Store.BOOKREVIEWS, bookReviewStore);

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

	@Override
	public Map<String, FramedGraph<?>> getRegisteredGraphs() {
		// System.out.println("RegisteredGraphs requested from " +
		// getClass().getName());
		Map<String, FramedGraph<?>> result = new HashMap<String, FramedGraph<?>>();
		result.put("demo", getDefault());
		return result;
	}

	@Override
	public Object processCommand(String namespace, String command, MultivaluedMap<String, String> params) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		if (command.equalsIgnoreCase("loadGraph")) {
			InputStream is = GraphFactory.class.getResourceAsStream("KevinBaconData.csv");
			BufferedReader br = null;
			String line = "";
			Pattern quote = Pattern.compile("\\\"");

			try {
				System.out.println("Loading Kevin Bacon data...");
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					String[] performance = quote.split(line);
					String performer = performance[1];
					String movietitle = performance[5];
					// performer = performer.substring(1);
					// movietitle = movietitle.substring(1, movietitle.length()
					// - 1);
					// System.out.println("Processing " + performer + " in " +
					// movietitle);
					Actor actor = (Actor) getDefault().addVertex(performer, Actor.class);
					actor.setName(performer);
					Movie movie = (Movie) getDefault().addVertex(movietitle, Movie.class);
					movie.setTitle(movietitle);
					actor.addMovie(movie);
				}
				((DFramedTransactionalGraph) getDefault()).commit();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		result.put("status", "starting");
		return result;
	}

	@Override
	public void registerCommandProcessor(String namespace, String command, ICommandProcessor processor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterCommandProcessor(ICommandProcessor processor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerConfigExtension(IExtConfiguration extConfig) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IExtConfiguration> getConfigExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

}
