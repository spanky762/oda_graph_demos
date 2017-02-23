package actorsmovies.model;

import org.openntf.domino.graph2.annotations.TypedProperty;
import org.openntf.domino.graph2.builtin.DVertexFrame;

import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Actor")
// @JavaHandlerClass(Actor.ActorImpl.class)
public interface Actor extends DVertexFrame {

	/*
	 * PROPERTIES
	 */

	@TypedProperty("Name")
	public String getName();


	@TypedProperty("Name")
	public void setName(String name);

	/*
	 * EDGES
	 */

	// @AdjacencyUnique(label = "StarsIn", direction = Direction.IN)
	// public Iterable<Movie> getMovies();
	//
	//
	// @AdjacencyUnique(label = "StarsIn", direction = Direction.IN)
	// public StarsIn addMovie(Movie movie);
	//
	//
	// @AdjacencyUnique(label = "StarsIn", direction = Direction.IN)
	// public void removeMovie(Movie movie);
	//
	//
	// @IncidenceUnique(label = "StarsIn", direction = Direction.IN)
	// public Iterable<StarsIn> getStarsIns();
	//
	//
	// @IncidenceUnique(label = "StarsIn", direction = Direction.IN)
	// public int countStarsIns();
	//
	//
	// @IncidenceUnique(label = "StarsIn", direction = Direction.IN)
	// public void removeStarsIn(StarsIn starsIn);

	// /*
	// * JAVAHANDLERS
	// */
	//
	// @JavaHandler
	// @TypedProperty("Title")
	// public String getTitle();
	//
	// /*
	// * ABSTRACT CLASS
	// * Only needed to provide implementation for @JavaHandler methods.
	// */
	// public abstract static class ActorImpl extends DVertexFrameImpl implements Actor, JavaHandlerContext<Vertex> {
	// @Override
	// public String getTitle() {
	// return this.getName();
	// }
	// }

}