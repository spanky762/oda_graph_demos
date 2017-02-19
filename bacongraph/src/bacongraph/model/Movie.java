package bacongraph.model;

import org.openntf.domino.graph2.annotations.AdjacencyUnique;
import org.openntf.domino.graph2.annotations.IncidenceUnique;
import org.openntf.domino.graph2.annotations.TypedProperty;
import org.openntf.domino.graph2.builtin.DEdgeFrame;
import org.openntf.domino.graph2.builtin.DVertexFrame;

import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.OutVertex;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Movie")
// @JavaHandlerClass(Movie.MovieImpl.class)
public interface Movie extends DVertexFrame {

	// public abstract static class MovieImpl extends DVertexFrameImpl implements Movie, JavaHandlerContext<Vertex> {
	//
	// }

	@TypeValue(StarsIn.LABEL)
	public static interface StarsIn extends DEdgeFrame {
		public static final String LABEL = "StarsIn";


		@TypedProperty("Character")
		public String getCharacter();


		@TypedProperty("Character")
		public void setCharacter(String character);


		@OutVertex
		public Movie getMovie();


		@InVertex
		public Actor getActor();
	}


	@TypedProperty("Title")
	public String getTitle();


	@TypedProperty("Title")
	public void setTitle(String title);


	@TypedProperty("Year")
	public String getYear();


	@TypedProperty("Year")
	public void setYear(String year);


	@AdjacencyUnique(label = StarsIn.LABEL)
	public Iterable<Actor> getActors();


	@AdjacencyUnique(label = StarsIn.LABEL)
	public StarsIn addActor(Actor actor);


	@AdjacencyUnique(label = StarsIn.LABEL)
	public void removeActor(Actor actor);


	@IncidenceUnique(label = StarsIn.LABEL)
	public Iterable<StarsIn> getStarsIns();


	@IncidenceUnique(label = StarsIn.LABEL)
	public int countStarsIns();


	@IncidenceUnique(label = StarsIn.LABEL)
	public void removeStarsIn(StarsIn starsIn);

}