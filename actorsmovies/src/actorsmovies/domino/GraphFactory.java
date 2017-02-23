package actorsmovies.domino;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openntf.domino.Session;
import org.openntf.domino.graph2.DElementStore;
import org.openntf.domino.graph2.impl.DConfiguration;
import org.openntf.domino.graph2.impl.DFramedGraphFactory;
import org.openntf.domino.graph2.impl.DGraph;
import org.openntf.domino.rest.service.AbstractGraphFactory;
import org.openntf.domino.rest.service.IGraphFactory;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.xots.Xots;

import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedTransactionalGraph;

import actorsmovies.util.ServletUtils;

public class GraphFactory extends AbstractGraphFactory implements IGraphFactory {
	public final static String GRAPH_NAME = "actorsmovies";
	protected static Map<String, FramedGraph<?>> registeredGraphs_;

	public static class GraphInitializer implements Runnable {
		public final GraphFactory parent_;
		@SuppressWarnings("rawtypes")
		FramedTransactionalGraph result_;


		public GraphInitializer(final GraphFactory parent) {
			this.parent_ = parent;
		}


		@Override
		public void run() {
			final String method = "run";
			final Class<? extends Object> type = this.getClass();
			ServletUtils.staticConsole(type, method, GraphFactory.GRAPH_NAME.concat(": Graph Initializer Starting"));
			try {
				final Session session = Factory.getSession(SessionType.NATIVE);
				Factory.setCurrentToSession(session);
				this.result_ = this.parent_.setupInitialGraph();
				session.switchSessionType(SessionType.CURRENT);
			} catch (final Throwable t) {
				t.printStackTrace();
			}

			ServletUtils.staticConsole(type, method, GraphFactory.GRAPH_NAME.concat(": Graph Initializer Finished"));
		}


		@SuppressWarnings("rawtypes")
		public FramedTransactionalGraph getResult() {
			return this.result_;
		}
	}


	public static FramedGraph<?> getNamedGraph(final String name) {
		return registeredGraphs_.get(name);
	}


	public GraphFactory() {
		// TODO Auto-generated constructor stub
	}


	@SuppressWarnings("rawtypes")
	protected FramedTransactionalGraph setupInitialGraph() {
		// This will run when the Graph is first referenced

		// NOTE that the Factory.getSession(SessionType.CURRENT) MUST return
		// a Session for a user authorized to create new Databases
		// if these NSFs don't exist yet
		FramedTransactionalGraph result = null;
		final DConfiguration config = new DConfiguration();
		final DGraph graph = new DGraph(config);

		final DElementStore actor = new org.openntf.domino.graph2.impl.DElementStore();
		actor.setStoreKey("demos/actorsmovies/actors.nsf");
		actor.addType(actorsmovies.model.Actor.class);
		config.addElementStore(actor);

		final DElementStore movie = new org.openntf.domino.graph2.impl.DElementStore();
		movie.setStoreKey("demos/actorsmovies/big/movies.nsf");
		movie.addType(actorsmovies.model.Movie.class);
		config.addElementStore(movie);

		final DFramedGraphFactory factory = new DFramedGraphFactory(config);
		result = factory.create(graph);
		return result;
	}


	@Override
	public Map<String, FramedGraph<?>> getRegisteredGraphs() {
		if (registeredGraphs_ == null) {
			synchronized (GraphFactory.class) {
				final Map<String, FramedGraph<?>> map = new HashMap<String, FramedGraph<?>>();
				final long start = new Date().getTime();
				final GraphInitializer init = new GraphInitializer(this);
				Xots.getService().execute(init);
				long runtime = new Date().getTime() - start;
				while ((init.getResult() == null) && (runtime < 60000)) {
					try {
						Thread.sleep(200);
						runtime = new Date().getTime() - start;
					} catch (final InterruptedException e) {
						System.out.println("Initiatlizer interrupted. Can't get graph.");
					}
				}
				map.put(GRAPH_NAME, init.getResult());
				registeredGraphs_ = Collections.unmodifiableMap(map);
			}
		}
		return registeredGraphs_;
	}

}
