package actorsmovies.model;

import org.openntf.domino.graph2.annotations.TypedProperty;
import org.openntf.domino.graph2.builtin.DEdgeFrame;

import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.OutVertex;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("StarsIn")
public interface StarsIn extends DEdgeFrame {

	@TypedProperty("Character")
	public String getCharacter();


	@TypedProperty("Character")
	public void setCharacter(String character);


	@OutVertex
	public Movie getMovie();


	@InVertex
	public Actor getActor();
}
