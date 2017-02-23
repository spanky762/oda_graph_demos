package actorsmovies.model;

import org.openntf.domino.graph2.annotations.AdjacencyUnique;
import org.openntf.domino.graph2.annotations.IncidenceUnique;
import org.openntf.domino.graph2.annotations.TypedProperty;
import org.openntf.domino.graph2.builtin.DVertexFrame;

import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("Movie")
public interface Movie extends DVertexFrame {

	// PROPERTIES

	@TypedProperty("Title")
	public String getTitle();


	@TypedProperty("Title")
	public void setTitle(String title);


	@TypedProperty("Year")
	public String getYear();


	@TypedProperty("Year")
	public void setYear(String year);


	// EDGES

	@AdjacencyUnique(label = "StarsIn")
	public Iterable<Actor> getActors();


	@AdjacencyUnique(label = "StarsIn")
	public StarsIn addActor(Actor actor);


	@AdjacencyUnique(label = "StarsIn")
	public void removeActor(Actor actor);


	@IncidenceUnique(label = "StarsIn")
	public Iterable<StarsIn> getStarsIns();


	@IncidenceUnique(label = "StarsIn")
	public int countStarsIns();


	@IncidenceUnique(label = "StarsIn")
	public void removeStarsIn(StarsIn starsIn);

}