package z.bacongraph.holding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		ArrayList<String> path_ = new ArrayList<String>();


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


		@Override
		public int getDistanceToKevinBacon() {
			return getDistanceTo("Kevin Bacon", 12);
		}


		@Override
		public int getDistanceTo(final String actorName, final int targetDepth, final int curDepth) {
			final List<Actor> costars = getCostars();
			if (targetDepth > curDepth) {
				for (final Actor costar : costars) {
					System.out.println("Processing " + costar.getName());
					final int chk = costar.getDistanceTo(actorName, targetDepth, curDepth + 1);
					if (chk < Integer.MAX_VALUE) { return chk; }
				}
			} else if (targetDepth == curDepth) {
				for (final Actor costar : costars) {
					System.out.println("Processing " + costar.getName());
					if (costar.getName().equals(actorName)) { return curDepth; }
				}
			}
			return Integer.MAX_VALUE;
		}


		@Override
		public int getDistanceTo(final String actorName, final int maxDepth) {

			System.out.println("Starting...");
			List<Actor> curActors = new ArrayList<Actor>();
			final List<String> checkedActors = new ArrayList<String>();
			final Map<Integer, List<Actor>> levelBreakdown = new HashMap<Integer, List<Actor>>();
			// final int chk2 = Integer.MAX_VALUE;
			for (int i = 1; i <= maxDepth; i++) {
				// If first loop, process this actor's costars
				if (curActors.isEmpty()) {
					curActors.add(this);
				}
				final List<Actor> nextActors = new ArrayList<Actor>();
				for (final Actor intActor : curActors) {
					final List<Actor> costars = (List<Actor>) intActor.getCostars();
					for (final Actor costar : costars) {
						if (!checkedActors.contains(costar.getName())) {
							if (costar.getName().equals(actorName)) {
								System.out.println("We have bacon!");
								// We have a route to Kevin Bacon!! Now go
								// backwards to get the path
								path_ = new ArrayList<String>();
								path_.add(actorName);
								System.out.println(i + ": " + actorName);
								for (final Movie movie : intActor.getMovies()) {
									for (final Actor checks : movie.getActors()) {
										if (costar.equals(checks)) {
											path_.add(
											        movie.getTitle() + " - " + movie.getYear() + getActorSummary(intActor, costar, movie));
											path_.add(intActor.getName());
											System.out.println((i - 1) + ": " + intActor.getName() + " also in " + movie.getTitle());
											break;
										}
									}
								}
								Actor nextLevelActor = intActor;
								if (i > 1) {
									for (int x = (i - 2); x > 0; x--) {
										final String checkActorName = nextLevelActor.getName();
										nextLevelActor = outputPath(levelBreakdown, nextLevelActor, x);
										if (nextLevelActor == null) {
											System.out.println("ERROR: No actor found at level " + x + " from " + checkActorName);
											x = -1;
										}
									}
								}
								for (final Movie movie : getMovies()) {
									for (final Actor checks : movie.getActors()) {
										if (nextLevelActor.equals(checks)) {
											path_.add(movie.getTitle() + " - " + movie.getYear()
											        + getActorSummary(this, nextLevelActor, movie));
											path_.add(getName());
											System.out.println(0 + ": " + getName() + " also in " + movie.getTitle());
											break;
										}
									}
								}

								Collections.reverse(path_);

								((DVertex) asVertex()).setFrameImplObject("path", path_);

								return i;
							}
							checkedActors.add(costar.getName());
							nextActors.add(costar);
						}
					}
					// System.out.println("Actors added " + nextActors.size());
				}

				// if (chk2 < Integer.MAX_VALUE) {
				// return chk2;
				// } else {
				curActors = new ArrayList<Actor>();
				curActors.addAll(nextActors);
				levelBreakdown.put(i, nextActors);
				// }
			}

			return -1;
		}


		@SuppressWarnings("unchecked")
		@Override
		public ArrayList<String> getPathToKevinBacon() {
			final ArrayList<String> path = (ArrayList<String>) ((DVertex) asVertex()).getFrameImplObject("path");
			return path;
		}


		private Actor outputPath(final Map<Integer, List<Actor>> levelBreakdown, final Actor nextLevelActor, final int x) {
			final List<Actor> prevLevel = levelBreakdown.get(x);
			for (final Actor proc : prevLevel) {
				for (final Movie movie : proc.getMovies()) {
					for (final Actor checks : movie.getActors()) {
						if (checks.equals(nextLevelActor)) {
							path_.add(movie.getTitle() + " - " + movie.getYear() + getActorSummary(proc, nextLevelActor, movie));
							path_.add(proc.getName());
							System.out.println(x + ": " + proc.getName() + " also in " + movie.getTitle());
							return proc;
						}
					}
				}
			}
			return null;
		}


		private String getActorSummary(final Actor actor1, final Actor actor2, final Movie movie) {
			String summary = " (" + actor1.getName() + " - ";
			for (final StarsIn stars : actor1.getStarsIns()) {
				if (stars.getMovie().equals(movie)) {
					if (null != stars.getCharacter()) {
						summary += stars.getCharacter();
					} else {
						summary += "no character stored";
					}
				}
			}
			summary += ", " + actor2.getName() + " - ";
			for (final StarsIn stars : actor2.getStarsIns()) {
				if (stars.getMovie().equals(movie)) {
					if (null != stars.getCharacter()) {
						summary += stars.getCharacter();
					} else {
						summary += "no character stored";
					}
				}
			}
			summary += ")";
			return summary;
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


	@JavaHandler
	public int getDistanceTo(String actorName, int targetDepth, int curDepth);


	@JavaHandler
	public int getDistanceTo(String actorName, int maxDepth);


	@JavaHandler
	@TypedProperty("DistanceToKevinBacon")
	public int getDistanceToKevinBacon();


	@JavaHandler
	@TypedProperty("PathToKevinBacon")
	public ArrayList<String> getPathToKevinBacon();

}