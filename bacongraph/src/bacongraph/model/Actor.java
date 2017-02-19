package bacongraph.model;

import java.util.ArrayList;
import java.util.List;

import org.openntf.domino.graph2.DVertex;
import org.openntf.domino.graph2.annotations.AdjacencyUnique;
import org.openntf.domino.graph2.annotations.IncidenceUnique;
import org.openntf.domino.graph2.annotations.TypedProperty;
import org.openntf.domino.graph2.builtin.DVertexFrame;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerClass;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import bacongraph.domino.ServletUtils;
import bacongraph.model.Movie.StarsIn;

@TypeValue("Actor")
@JavaHandlerClass(Actor.ActorImpl.class)
public interface Actor extends DVertexFrame {

	public abstract static class ActorImpl extends DVertexFrameImpl implements Actor, JavaHandlerContext<Vertex> {

		@Override
		public String getTitle() {
			return this.getName();
		}


		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public List<Actor> getCostars() {
			final String method = "getCostars()";
			final Class<? extends Object> type = null;

			List<Actor> result = (List) ((DVertex) asVertex()).getFrameImplObject("costars");
			if (result == null) {
				ServletUtils.staticConsole(type, method, "Getting Costars for " + this.getName());
				result = new ArrayList<Actor>();
				for (final Movie movie : getMovies()) {
					// System.out.println("\t Checking " + movie.getTitle());
					for (final Actor actor : movie.getActors()) {
						if (!getName().equals(actor.getName())) {
							result.add(actor);
						}
					}
				}

				// System.out.println(" ");
				result.remove(this);
				((DVertex) asVertex()).setFrameImplObject("costars", result);
			}
			return result;
		}


		@Override
		public int getCostarCount() {
			return getCostars().size();
		}

	}


	@JavaHandler
	@TypedProperty("Title")
	public String getTitle();


	@TypedProperty("Name")
	public String getName();


	@TypedProperty("Name")
	public void setName(String name);


	@AdjacencyUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public Iterable<Movie> getMovies();


	@AdjacencyUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public StarsIn addMovie(Movie movie);


	@AdjacencyUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public void removeMovie(Movie movie);


	@IncidenceUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public Iterable<StarsIn> getStarsIns();


	@IncidenceUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public int countStarsIns();


	@IncidenceUnique(label = StarsIn.LABEL, direction = Direction.IN)
	public void removeStarsIn(StarsIn starsIn);


	@JavaHandler
	public Iterable<Actor> getCostars();


	@JavaHandler
	@TypedProperty("CostarCount")
	public int getCostarCount();

}